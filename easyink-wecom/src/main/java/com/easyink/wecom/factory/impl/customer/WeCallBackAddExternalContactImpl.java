package com.easyink.wecom.factory.impl.customer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.constant.redeemcode.RedeemCodeConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.*;
import com.easyink.common.enums.code.WelcomeMsgTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.lock.LockUtil;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.AddWeMaterialDTO;
import com.easyink.wecom.domain.dto.WeWelcomeMsg;
import com.easyink.wecom.domain.dto.common.*;
import com.easyink.wecom.domain.dto.customer.GetExternalDetailResp;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDTO;
import com.easyink.wecom.domain.vo.SelectWeEmplyCodeWelcomeMsgVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.domain.vo.welcomemsg.WeEmployMaterialVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.mapper.WeCustomerTrajectoryMapper;
import com.easyink.wecom.mapper.WeEmpleCodeChannelMapper;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.autotag.WeAutoTagRuleHitCustomerRecordService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeService;
import com.easyink.wecom.utils.AttachmentService;
import com.easyink.wecom.utils.redis.CustomerRedisCache;
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 新增客户事件
 * @date 2021/1/20 23:18
 **/
@Slf4j
@Component("add_external_contact")
public class WeCallBackAddExternalContactImpl extends WeEventStrategy {
    @Autowired
    private WeCustomerService weCustomerService;
    @Autowired
    private WeEmpleCodeTagService weEmpleCodeTagService;
    @Autowired
    private WeEmpleCodeService weEmpleCodeService;
    @Autowired
    private WeFlowerCustomerRelService weFlowerCustomerRelService;
    @Autowired
    private WeMaterialService weMaterialService;
    @Autowired
    private RuoYiConfig ruoYiConfig;
    @Autowired
    private WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;

    @Autowired
    private WeTagService weTagService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private WeCustomerClient weCustomerClient;

    @Autowired
    private WeCustomerTrajectoryService weCustomerTrajectoryService;
    @Autowired
    private WeMsgTlpService weMsgTlpService;
    @Autowired
    private WeMsgTlpMaterialService weMsgTlpMaterialService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private WeAutoTagRuleHitCustomerRecordService weAutoTagRuleHitCustomerRecordService;
    @Autowired
    private WeRedeemCodeService weRedeemCodeService;
    @Autowired
    private WeEmpleCodeMapper weEmpleCodeMapper;
    @Autowired
    private WeCustomerTrajectoryMapper weCustomerTrajectoryMapper;

    private final WeUserService weUserService;
    @Autowired
    private CustomerRedisCache customerRedisCache;

    private final EmpleStatisticRedisCache empleStatisticRedisCache;
    private final WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper;
    private final WeCorpAccountService weCorpAccountService;
    private final WeEmpleCodeChannelMapper weEmpleCodeChannelMapper;
    private final CustomerAssistantService customerAssistantService;

    @Autowired
    public WeCallBackAddExternalContactImpl(WeUserService weUserService, EmpleStatisticRedisCache empleStatisticRedisCache, WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper, WeCorpAccountService weCorpAccountService, WeEmpleCodeChannelMapper weEmpleCodeChannelMapper, CustomerAssistantService customerAssistantService) {
        this.weUserService = weUserService;
        this.empleStatisticRedisCache = empleStatisticRedisCache;
        this.weEmpleCodeStatisticMapper = weEmpleCodeStatisticMapper;
        this.weCorpAccountService = weCorpAccountService;
        this.weEmpleCodeChannelMapper = weEmpleCodeChannelMapper;
        this.customerAssistantService = customerAssistantService;
    }


    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (!checkParams(message)) {
            return;
        }
        String corpId = message.getToUserName();
        // 收到新增客户回调后,先进行欢迎语处理
        if (message.getState() != null && message.getWelcomeCode() != null && !isFission(message.getState())) {
            if (isAssistantState(message.getState())) {
                // 获客链接欢迎语处理
                sendCustomerAssistantWelcomeMsg(message.getState(), message.getWelcomeCode(), message.getUserId(), message.getExternalUserId(), corpId);
            } else {
                // 活码欢迎语处理
                sendEmpleCodeWelcomeMsg(message.getState(), message.getWelcomeCode(), message.getUserId(), message.getExternalUserId(), corpId);
            }
        } else if (message.getWelcomeCode() != null) {
            // 配置的欢迎语处理
            otherHandle(message.getWelcomeCode(), message.getUserId(), message.getExternalUserId(), corpId);
        } else {
            log.error("[{}]:回调处理,发送欢迎语失败,message:{}", message.getChangeType(), message);
        }
        //  存入redis 后续由定时任务与编辑客户回调一起处理,避免同时处理导致的锁表 Tower 任务: 好友活码打标签失败 ( https://tower.im/teams/636204/todos/70202 )
        customerRedisCache.saveCallback(message.getToUserName(), message.getUserId(), message.getExternalUserId(), message);
    }

    /**
     * 判断是否是获客链接添加的state
     *
     * @param state 来源state
     * @return true 是，false 不是
     */
    private Boolean isAssistantState(String state) {
        return state.startsWith(CustomerAssistantConstants.STATE_PREFIX);
    }

    /**
     * 添加客户回调处理
     *
     * @param message 回调消息体
     */
    public void addHandle(WxCpXmlMessageVO message) {
        String corpId = message.getToUserName();
        // 添加客户回调处理
        try {
            // 先查询数据库中是否已经存在此客户
            List<String> lossExternalUserId = new ArrayList<>();
            LambdaQueryWrapper<WeFlowerCustomerRel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WeFlowerCustomerRel::getCorpId, corpId);
            queryWrapper.eq(WeFlowerCustomerRel::getUserId, message.getUserId());
            weFlowerCustomerRelService.list(queryWrapper)
                                      .forEach(item -> lossExternalUserId.add(item.getExternalUserid()));
            // 若存在，则直接更新状态，不从远端获取信息
            if (lossExternalUserId.contains(message.getExternalUserId())) {
                weFlowerCustomerRelService.updateLossExternalUser(corpId, message.getUserId(), message.getExternalUserId());
            } else {
                weCustomerService.updateExternalContactV2(corpId, message.getUserId(), message.getExternalUserId());
            }
        } catch (Exception e) {
            log.error("[{}]:回调处理,更新客户信息异常,message:{},e:{}", message.getChangeType(), message, ExceptionUtils.getStackTrace(e));
        }
        weAutoTagRuleHitCustomerRecordService.makeTagToNewCustomer(message.getExternalUserId(), message.getUserId(), corpId);
        // state存在，且不是任务裂变活码前缀，也不是获客链接前缀
        if (message.getState() != null  && !isFission(message.getState()) && !message.getState().startsWith(CustomerAssistantConstants.STATE_PREFIX)) {
            // 活码处理
            empleCodeHandle(message.getState(), message.getUserId(), message.getExternalUserId(), corpId);
        }
        // 获客链接处理
        if (message.getState() != null && message.getState().startsWith(CustomerAssistantConstants.STATE_PREFIX)) {
            customerAssistantService.callBackAddAssistantHandle(message.getState(), message.getUserId(), message.getExternalUserId(), corpId);
        }
        // 客户轨迹记录 : 添加员工
        weCustomerTrajectoryService.saveActivityRecord(corpId, message.getUserId(), message.getExternalUserId(), CustomerTrajectoryEnums.SubType.ADD_USER.getType());
    }

    /**
     * 发送获客链接的欢迎语
     *
     * @param state          活码state
     * @param welcomeCode    欢迎语code
     * @param userId         员工id
     * @param externalUserId 客户id
     * @param corpId         企业id
     */
    private void sendCustomerAssistantWelcomeMsg(String state, String welcomeCode, String userId, String externalUserId, String corpId) {
        WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder = WeWelcomeMsg.builder().welcome_code(welcomeCode);
        SelectWeEmplyCodeWelcomeMsgVO messageMap = weEmpleCodeService.selectWelcomeMsgByState(state, corpId);
        if (messageMap == null) {
            // 将"hk_"前缀截取掉，得到渠道id作为state信息
            String channelId = state.replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY);
            // 根据渠道id获取渠道对应的获客链接id信息
            WeEmpleCodeChannel channel = weEmpleCodeChannelMapper.getChannelById(channelId);
            // 若根据渠道id未查询到信息，则表示不是从获客链接添加的客户，停止处理
            if (channel == null) {
                log.info("[获客链接发送欢迎语] 未查询到该state对应的获客链接信息，state:{},corpId:{},userId:{},externalUserId:{}", state, corpId, userId, externalUserId);
                return;
            }
            messageMap = weEmpleCodeService.selectWelcomeMsgById(String.valueOf(channel.getEmpleCodeId()), corpId);
        }
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            weEmpleCodeService.buildCustomerAssistantWelcomeMsg(messageMap, corpId);
            //给好友发送消息
            SelectWeEmplyCodeWelcomeMsgVO finalMessageMap = messageMap;
            CompletableFuture.runAsync(() -> {
                try {
                    sendMessageToNewExternalUserId(weWelcomeMsgBuilder, finalMessageMap, getCustomerName(corpId, externalUserId), corpId, userId, externalUserId, state);
                } catch (Exception e) {
                    log.error("异步发送欢迎语消息异常：ex:{}", ExceptionUtils.getStackTrace(e));
                }
            });
        } else if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            handleRedeemCodeWelcomeMsg(state, userId, externalUserId, corpId, weWelcomeMsgBuilder, messageMap, getCustomerName(corpId, externalUserId));
        }
    }

    private boolean checkParams(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getUserId(), message.getExternalUserId())) {
            log.error("[add_external_contact]:回调数据不完整,message:{}", message);
            return false;
        }
        if (Boolean.FALSE.equals(redisCache.addLock(message.getUniqueKey(message.getExternalUserId()), "", Constants.CALLBACK_HANDLE_LOCK_TIME))) {
            log.info("[{}]添加客户事件回调,该回调已处理,不重复处理,message:{}", message.getChangeType(), message);
            // 不重复处理
            return false;
        }
        return true;
    }

    /**
     * 其他欢迎语处理
     *
     * @param welcomeCode    欢迎语code
     * @param userId         成员id
     * @param externalUserId 客户id
     * @param corpId         企业id
     */
    private void otherHandle(String welcomeCode, String userId, String externalUserId, String corpId) {
        log.info("执行发送非活码欢迎语欢迎语otherHandle>>>>>>>>>>>>>>>");
        log.info("[非活码欢迎语] welcomeCode:{}, userId:{}, externalUserId:{}, corpId:{}", welcomeCode, userId, externalUserId, corpId);
        WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder = WeWelcomeMsg.builder().welcome_code(welcomeCode);
        // 查询外部联系人与通讯录关系数据
        GetExternalDetailResp customerInfo = getCustomerInfo(corpId, externalUserId);
        if (customerInfo == null || customerInfo.getExternalContact().getName() == null || customerInfo.getExternalContact().getGender() == null || CollectionUtils.isEmpty(customerInfo.getFollow_user())) {
            log.info("[非活码欢迎语] 缺少客户信息，不发送。customerInfo:{}", customerInfo);
            return;
        }
        // 客户名称
        String customerName = customerInfo.getExternalContact().getName();
        // 客户性别
        Integer gender = customerInfo.getExternalContact().getGender();
        // 客户来源，寻找客户详情信息中与添加员工对应的关系信息，员工-客户关系是唯一的，所以直接取列表第一个
        String addWay = customerInfo.getFollow_user().stream()
                                                     .filter(item -> item.getUserId().equals(userId))
                                                     .collect(Collectors.toList())
                                                     .get(0).getAdd_way();
        CompletableFuture.runAsync(() -> {
            try {
                WeEmployMaterialVO weEmployMaterialVO = weMsgTlpService.selectMaterialByUserId(userId, corpId, addWay, gender);
                // 当前员工存在命中的附件
                if (weEmployMaterialVO != null
                        && (CollectionUtils.isNotEmpty(weEmployMaterialVO.getWeMsgTlpMaterialList()) || StringUtils.isNotEmpty(weEmployMaterialVO.getDefaultMsg()))) {
                    // 构建欢迎语并发送
                    buildAndSendOtherWelcomeMsg(weEmployMaterialVO, userId, externalUserId, corpId, weWelcomeMsgBuilder, customerName);
                    log.info("好友欢迎语发送成功>>>>>>>>>>>>>>>");
                }
            } catch (Exception e) {
                log.error("异步发送欢迎语消息异常：ex:{}", ExceptionUtils.getStackTrace(e));
            }
        });
    }

    /**
     * 构建素材并发送
     */
    private void buildAndSendOtherWelcomeMsg(WeEmployMaterialVO weEmployMaterialVO, String userId, String externalUserId, String corpId, WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder, String remark) {
        // 组装数据
        WeWelcomeMsg weWelcomeMsg = weMsgTlpMaterialService.buildWeWelcomeMsg(weEmployMaterialVO.getDefaultMsg(), weEmployMaterialVO.getWeMsgTlpMaterialList(), weWelcomeMsgBuilder, userId, externalUserId, corpId, remark);
        // 调用企业微信接口发送欢迎语消息
        weCustomerService.sendWelcomeMsg(weWelcomeMsg, corpId);
    }

    /**
     * 发送活码的欢迎语
     *
     * @param state          活码state
     * @param welcomeCode    欢迎语code
     * @param userId         员工id
     * @param externalUserId 客户id
     * @param corpId         企业id
     */
    private void sendEmpleCodeWelcomeMsg(String state, String welcomeCode, String userId, String externalUserId, String corpId) {
        WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder = WeWelcomeMsg.builder().welcome_code(welcomeCode);
        SelectWeEmplyCodeWelcomeMsgVO messageMap = weEmpleCodeService.selectWelcomeMsgByState(state, corpId);
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            weEmpleCodeService.buildCommonWelcomeMsg(messageMap, corpId, externalUserId);
            //给好友发送消息
            CompletableFuture.runAsync(() -> {
                try {
                    sendMessageToNewExternalUserId(weWelcomeMsgBuilder, messageMap, getCustomerName(corpId,externalUserId), corpId, userId, externalUserId, state);
                } catch (Exception e) {
                    log.error("异步发送欢迎语消息异常：ex:{}", ExceptionUtils.getStackTrace(e));
                }
            });
        } else if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            handleRedeemCodeWelcomeMsg(state, userId, externalUserId, corpId, weWelcomeMsgBuilder, messageMap, getCustomerName(corpId,externalUserId));
        }
    }

    /**
     * 获取客户名称
     *
     * @param corpId         企业Id
     * @param externalUserid 客户id
     * @return 客户名称
     */
    public String getCustomerName(String corpId, String externalUserid) {
        GetExternalDetailResp resp = weCustomerClient.getV2(externalUserid, corpId);
        if (resp == null || resp.getExternalContact() == null || StringUtils.isBlank(resp.getExternalContact()
                                                                                         .getName())) {
            return StringUtils.EMPTY;
        }
        return resp.getExternalContact().getName();
    }

    /**
     * 获取客户名称
     *
     * @param corpId         企业Id
     * @param externalUserid 客户id
     * @return 客户名称
     */
    public GetExternalDetailResp getCustomerInfo(String corpId, String externalUserid) {
        if (StringUtils.isAnyBlank(corpId, externalUserid)) {
            return null;
        }
        return weCustomerClient.getV2(externalUserid, corpId);
    }


    /**
     * 活码欢迎语发送
     *
     * @param state          渠道
     * @param userId         成员id
     * @param externalUserId 客户id
     */
    private void empleCodeHandle(String state, String userId, String externalUserId, String corpId) {
        log.info("活码处理开始empleCodeHandle>>>>>>>>>>>>>>>");
        try {
            SelectWeEmplyCodeWelcomeMsgVO messageMap = weEmpleCodeService.selectWelcomeMsgByState(state, corpId);
            if (null != messageMap && org.apache.commons.lang3.StringUtils.isNotBlank(messageMap.getEmpleCodeId())) {
                String empleCodeId = messageMap.getEmpleCodeId();
                //更新活码数据统计
                weEmpleCodeAnalyseService.saveWeEmpleCodeAnalyse(corpId, userId, externalUserId, empleCodeId, true);
                // 更新Redis中的数据
                empleStatisticRedisCache.addNewCustomerCnt(corpId, DateUtils.dateTime(new Date()), Long.valueOf(empleCodeId), userId);
                //查询外部联系人与通讯录关系数据
                WeFlowerCustomerRel weFlowerCustomerRel = weFlowerCustomerRelService
                        .getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                                .eq(WeFlowerCustomerRel::getUserId, userId)
                                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                                .eq(WeFlowerCustomerRel::getStatus, 0).last(GenConstants.LIMIT_1)
                        );
                // 查询外部联系人的信息
                WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getExternalUserid, externalUserId));
                //更新活码添加方式
                weFlowerCustomerRel.setState(state);
                weFlowerCustomerRelService.updateById(weFlowerCustomerRel);
                //为外部联系人添加员工活码标签
                setEmplyCodeTag(weFlowerCustomerRel, empleCodeId, messageMap.getTagFlag());
                // 打标签后休眠1S , 避免出现打标签后又打备注提示接口调用频繁 Tower 任务: 客户扫活码加好友之后没有自动备注 ( https://tower.im/teams/636204/todos/69053 )
                ThreadUtil.safeSleep(1000L);
                //判断是否需要设置备注
                setEmplyCodeExternalUserRemark(state, userId, externalUserId, corpId, messageMap.getRemarkType(), messageMap.getRemarkName(), weCustomer.getName());
            }
        } catch (Exception e) {
            log.error("empleCodeHandle error!! e={}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 活码统计数据记录
     *
     * @param corpId 企业ID
     * @param userId 员工ID
     * @param empleCodeId 活码ID
     */
    private void empleStatisticCount(String corpId, String userId, String empleCodeId) {
        String date = DateUtils.dateTime(new Date());
        // 获取当前日期下活码-员工对应的统计数据
        WeEmpleCodeStatistic historyData = weEmpleCodeStatisticMapper.selectOne(new LambdaQueryWrapper<WeEmpleCodeStatistic>()
                .eq(WeEmpleCodeStatistic::getCorpId, corpId)
                .eq(WeEmpleCodeStatistic::getUserId, userId)
                .eq(WeEmpleCodeStatistic::getEmpleCodeId, empleCodeId)
                .eq(WeEmpleCodeStatistic::getTime, date)
        );
        // 没有历史数据，创建一条新数据
        if (historyData == null) {
            WeEmpleCodeStatistic newData = new WeEmpleCodeStatistic(corpId, Long.valueOf(empleCodeId), userId, date);
            newData.addHandle();
            // 保存新数据
            weEmpleCodeStatisticMapper.insert(newData);
        } else {
            // 存在数据，则将新增客户数+1
            List<WeEmpleCodeStatistic> dataList = new ArrayList<>();
            historyData.setNewCustomerCnt(historyData.getNewCustomerCnt() + 1);
            dataList.add(historyData);
            // 更新数据
            weEmpleCodeStatisticMapper.batchInsertOrUpdate(dataList);
        }
    }

    /**
     * 处理兑换码欢迎语
     *
     * @param state               渠道
     * @param userId              员工id
     * @param externalUserId      外部联系人id
     * @param corpId              企业id
     * @param weWelcomeMsgBuilder {@link WeWelcomeMsg.WeWelcomeMsgBuilder}
     * @param messageMap          {@link SelectWeEmplyCodeWelcomeMsgVO}
     * @param customerName        {@link WeFlowerCustomerRel}
     */
    private void handleRedeemCodeWelcomeMsg(String state, String userId, String externalUserId, String corpId, WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder, SelectWeEmplyCodeWelcomeMsgVO messageMap, String customerName) {
        RLock rLock = null;
        boolean isHaveLock = false;
        try {
            final String redeemCodeKey = RedeemCodeConstants.getRedeemCodeKey(corpId, messageMap.getCodeActivityId());
            rLock = LockUtil.getLock(redeemCodeKey);
            isHaveLock = rLock.tryLock(RedeemCodeConstants.CODE_WAIT_TIME, RedeemCodeConstants.CODE_LEASE_TIME, TimeUnit.SECONDS);
            if (isHaveLock) {
                weEmpleCodeService.buildRedeemCodeActivityWelcomeMsg(messageMap, corpId, externalUserId);
                //同步发送消息
                sendMessageToNewExternalUserId(weWelcomeMsgBuilder, messageMap, customerName, corpId, userId, externalUserId, state);
                log.info("[欢迎语回调]活动欢迎语处理完成，活动id:{},corpId:{}}", messageMap.getCodeActivityId(), corpId);
            }
        } catch (InterruptedException e) {
            log.error("[欢迎语回调]活动欢迎语获取锁失败,e:{},活动id:{},corpId:{}", ExceptionUtils.getStackTrace(e), messageMap.getCodeActivityId(), corpId);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("[欢迎语回调]拼装活动欢迎语 或 同步发送欢迎语消息异常,e:{},活动id:{},corpId:{}", ExceptionUtils.getStackTrace(e), messageMap.getCodeActivityId(), corpId);
        } finally {
            if(rLock != null && rLock.isHeldByCurrentThread()){
                rLock.unlock();
            }
        }
    }


    /**
     * 给员工活码加入的客户设置备注
     *
     * @param state          回调state
     * @param userId         员工userId
     * @param externalUserId 客户
     * @param corpId         企业ID
     * @param remarkType     备注类型
     * @param remarkName     设置的备注名
     * @param nickName       客户昵称
     */
    private void setEmplyCodeExternalUserRemark(String state, String userId, String externalUserId, String corpId,
                                                Integer remarkType, String remarkName, String nickName) {
        if (org.apache.commons.lang3.StringUtils.isBlank(userId)
                || org.apache.commons.lang3.StringUtils.isBlank(externalUserId)
                || org.apache.commons.lang3.StringUtils.isBlank(corpId)
                || remarkType == null) {
            log.error("setEmplyCodeExternalUserRemark param error!! state={},userId={},externalUserId={},corpId={},remarkType={}", state, userId, externalUserId, corpId, remarkType);
            return;
        }
        if (WeEmployCodeRemarkTypeEnum.NO.getRemarkType().equals(remarkType) || org.apache.commons.lang3.StringUtils.isBlank(remarkName)) {
            log.info("setEmplyCodeExternalUserRemark. remarkType={},remarkName={}", remarkType, remarkName);
            return;
        }
        String newRemark;
        log.info("setEmplyCodeExternalUserRemark 员工活码 开始设置新客户备注！");
        if (WeEmployCodeRemarkTypeEnum.BEFORT_NICKNAME.getRemarkType().equals(remarkType)) {
            newRemark = remarkName + "-" + nickName;
        } else {
            newRemark = nickName + "-" + remarkName;
        }
        WeCustomer weCustomer = new WeCustomer();
        weCustomer.setCorpId(corpId);
        weCustomer.setUserId(userId);
        weCustomer.setExternalUserid(externalUserId);
        weCustomer.setRemark(newRemark);
        try {
            weCustomerService.updateWeCustomerRemark(weCustomer);
        } catch (Exception e) {
            log.error("setEmplyCodeExternalUserRemark error!! corpId={},userId={},externalUserId={},", corpId, userId, externalUserId);
        }
    }


    /**
     * 设置员工活码标签
     *
     * @param weFlowerCustomerRel weFlowerCustomerRel
     * @param empleCodeId         员工活码主键ID
     * @param tagFlag             是否打标签
     */
    private void setEmplyCodeTag(WeFlowerCustomerRel weFlowerCustomerRel, String empleCodeId, Boolean tagFlag) {
        if (weFlowerCustomerRel == null || org.apache.commons.lang3.StringUtils.isBlank(empleCodeId) || tagFlag == null) {
            log.warn("setEmplyCodeTag warn!! empleCodeId={},tagFlag={},weFlowerCustomerRel={}", empleCodeId, tagFlag, JSONObject.toJSONString(weFlowerCustomerRel));
            return;
        }
        if (!tagFlag) {
            log.info("setEmplyCodeTag. empleCodeId={} 未开启打标签");
            return;
        }
        try {
            // 获取员工详情
            WeUser weUser = weUserService.getUserDetail(weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getUserId());
            if (weUser == null) {
                log.info("[setEmplyCodeTag] 员工活码,查询不到员工信息,corpId:{},userId:{}", weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getUserId());
                return;
            }
            //查询活码对应标签
            List<WeEmpleCodeTag> tagList = weEmpleCodeTagService.list(new LambdaQueryWrapper<WeEmpleCodeTag>().eq(WeEmpleCodeTag::getEmpleCodeId, empleCodeId));
            //存在则打标签
            if (CollectionUtils.isNotEmpty(tagList)) {
                log.info("setEmplyCodeTag 员工活码 开始批量打标签！");
                //查询这个tagId对应的groupId
                List<String> tagIdList = tagList.stream().map(WeEmpleCodeTag::getTagId).filter(Objects::nonNull).collect(Collectors.toList());
                // 获取有效的标签名称
                List<String> tagNameList = weTagService.getTagNameByIds(tagIdList);
                weCustomerService.singleMarkLabel(weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getUserId(), weFlowerCustomerRel.getExternalUserid(), tagIdList, weUser.getName());
                recordCodeTag(weFlowerCustomerRel.getCorpId(),weUser,weFlowerCustomerRel.getExternalUserid(),tagNameList,empleCodeId);
            }
        } catch (Exception e) {
            log.error("setEmplyCodeTag error!! empleCodeId={},e={}", empleCodeId, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 记录扫描员工活码打标签的信息动态
     *
     * @param corpId 公司id
     * @param weUser 员工信息
     * @param externalUserId 客户id
     * @param tagNameList 标签列表
     * @param empleCodeId 活码id
     */
    public void recordCodeTag(String corpId, WeUser weUser, String externalUserId, List<String> tagNameList,String empleCodeId){
        if (StringUtils.isAnyBlank(corpId, externalUserId) || CollUtil.isEmpty(tagNameList)||Objects.isNull(weUser)) {
            log.info("记录工活码打标签的信息动态时员工，客户id，公司id,标签列表,活码id不能为空，userId：{}，externalUserId：{}，corpId：{}，addTagIds: {},empleCodeId:{}", weUser, externalUserId, corpId, tagNameList,empleCodeId);
            return;
        }
        WeEmpleCode weEmpleCode=weEmpleCodeMapper.selectById(empleCodeId);
        if (Objects.isNull(weEmpleCode)){
            log.info("记录工活码打标签的信息动态时,查询员工活码信息异常");
            return;
        }
        TagRecordUtil tagRecordUtil=new TagRecordUtil();
        String content = tagRecordUtil.buildCodeContent(weEmpleCode.getScenario(),weUser.getName());
        String detail = String.join(",", tagNameList);
        //保存信息动态
        weCustomerTrajectoryService.saveCustomerTrajectory(corpId,weUser.getUserId(),externalUserId,content,detail);
    }

    /**
     * 给好友发送消息
     */
    private void sendMessageToNewExternalUserId(WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder,
                                                SelectWeEmplyCodeWelcomeMsgVO messageMap, String remark,
                                                String corpId, String userId, String externalUserId, String state) {
        log.debug(">>>>>>>>>欢迎语查询结果：{}", JSON.toJSONString(messageMap));
        // 1.构建欢迎语
        // 存在欢迎语则替换标签文本进行构建
        String replyText = weMsgTlpMaterialService.replyTextIfNecessary(messageMap.getWelcomeMsg(), remark, messageMap.getRedeemCode(), externalUserId, userId, corpId);
        Optional.ofNullable(replyText).ifPresent(text -> weWelcomeMsgBuilder.text(Text.builder().content(text).build()));

        // 2.构建附件
        List<Attachment> attachmentList = new ArrayList<>();
        // 2.1 新客先添加入群二维码
        if (EmployCodeSourceEnum.NEW_GROUP.getSource().equals(messageMap.getSource())) {
            // 新客拉群创建的员工活码欢迎语图片(群活码图片)
            String codeUrl = messageMap.getGroupCodeUrl();
            if (StringUtils.isNotBlank(codeUrl)) {
                buildWelcomeMsgImg(corpId, codeUrl, getGroupCodeFilename(codeUrl, corpId), attachmentList);
            }
        }
        // 欢迎语发送素材
        if (CollectionUtils.isNotEmpty(messageMap.getMaterialList())) {
            //数量超出上限抛异常
            if (messageMap.getMaterialList().size() > WeConstans.MAX_ATTACHMENT_NUM) {
                throw new CustomException(ResultTip.TIP_ATTACHMENT_OVER);
            }
            buildWeEmplyWelcomeMsg(messageMap.getSource(), messageMap.getScenario(), userId, state, corpId, messageMap.getMaterialList(), attachmentList);
        }

        // 3.调用企业微信接口发送欢迎语
        weCustomerService.sendWelcomeMsg(weWelcomeMsgBuilder.attachments(attachmentList).build(), corpId);

        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            // 4.更新兑换码的发送状态
            if (StringUtils.isNotBlank(messageMap.getRedeemCode())) {
                weRedeemCodeService.updateRedeemCode(WeRedeemCodeDTO.builder()
                        .activityId(Long.valueOf(messageMap.getCodeActivityId()))
                        .code(messageMap.getRedeemCode())
                        .corpId(corpId)
                        .receiveUserId(externalUserId).build());
            }
        }
    }

    /**
     * 获取群聊二维码的图片文件名称
     *
     * @param codeUrl 群二维码URL
     * @param corpId 企业ID
     * @return 图片文件名称
     */
    private String getGroupCodeFilename(String codeUrl, String corpId) {
        if (StringUtils.isAnyBlank(codeUrl, corpId)) {
            log.info("[入群欢迎语] 获取不到发送的群二维码信息，codeUrl:{}, corpId:{}", codeUrl, corpId);
            return StringUtils.EMPTY;
        }
        if (!codeUrl.startsWith(Constants.RESOURCE_PREFIX)) {
            return codeUrl.replaceAll(ruoYiConfig.getFile().getCos().getCosImgUrlPrefix(), StringUtils.EMPTY);
        }
        // 获取企业配置的H5Domain域名信息
        WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (weCorpAccount == null) {
            return StringUtils.EMPTY;
        }
        return codeUrl.replaceAll(weCorpAccount.getH5DoMainName() + WeConstans.SLASH + Constants.RESOURCE_PREFIX, StringUtils.EMPTY);
    }

    private boolean isFission(String str) {
        return str.contains(WeConstans.FISSION_PREFIX);
    }

    /**
     * 构建欢迎语的图片部分
     *
     * @param picUrl         图片链接
     * @param fileName       图片名称
     * @param attachmentList
     */
    private void buildWelcomeMsgImg(String corpId, String picUrl, String fileName, List<Attachment> attachmentList) {

        AttachmentParam param = AttachmentParam.builder().picUrl(picUrl).content(fileName).typeEnum(AttachmentTypeEnum.IMAGE).build();
        Attachments attachments = attachmentService.buildAttachment(param, corpId);
        if (attachments != null) {
            attachmentList.add(attachments);
        }
//        Optional.ofNullable(weMediaDto).ifPresent(media -> builder.image(Image.builder().media_id(media.getMedia_id()).pic_url(media.getUrl()).build()));
    }


    private void buildWeEmplyWelcomeMsg(Integer source, String scenario, String userId, String state, String corpId, List<AddWeMaterialDTO> weMaterialList, List<Attachment> attachmentList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(weMaterialList)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        Attachments attachments;
        for (AddWeMaterialDTO weMaterialVO : weMaterialList) {
            AttachmentTypeEnum typeEnum = AttachmentTypeEnum.mappingFromGroupMessageType(weMaterialVO.getMediaType());
            if (typeEnum == null) {
                log.error("type is error!!!, type: {}", weMaterialVO.getMediaType());
                continue;
            }
            AttachmentParam param = AttachmentParam.costFromWeMaterialByType(source, scenario, userId, corpId, weMaterialVO, typeEnum);
            attachments = attachmentService.buildAttachment(param, corpId);
//            attachments = weMsgTlpMaterialService.buildByWelcomeMsgType(param.getContent(), param.getPicUrl(), param.getDescription(), param.getUrl(), typeEnum, corpId);
            if (attachments != null) {
                attachmentList.add(attachments);
            } else {
                log.error("type error!! state={}, mediaType={}", state, weMaterialVO.getMediaType());
            }
        }
    }


    /**
     * 发送视频
     *
     * @param weMaterialDTO 素材内容
     * @return JSONObject
     */
    private Attachment sendLink(AddWeMaterialDTO weMaterialDTO) {
        Attachments attachments = new Attachments();
        attachments.setMsgtype(AttachmentTypeEnum.LINK.getTypeStr());
        attachments.setLink(Link.builder()
                .title(weMaterialDTO.getMaterialName())
                .picurl(WeConstans.DEFAULT_VIDEO_COVER_URL)
                .desc(WeConstans.CLICK_SEE_VIDEO)
                .url(weMaterialDTO.getMaterialUrl())
                .build());

        return attachments;
    }


}
