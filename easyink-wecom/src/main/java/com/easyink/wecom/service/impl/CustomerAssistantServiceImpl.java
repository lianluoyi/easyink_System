package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.*;
import com.easyink.common.enums.code.WelcomeMsgTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.client.WeCustomerAcquisitionClient;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.emplecode.*;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.vo.SelectWeEmplyCodeWelcomeMsgVO;
import com.easyink.wecom.domain.vo.emple.*;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeEmpleCodeAnalyseMapper;
import com.easyink.wecom.mapper.WeEmpleCodeChannelMapper;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.utils.redis.CustomerAssistantRedisCache;
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 获客链接Service业务层
 *
 * @author lichaoyu
 * @date 2023/8/23 10:36
 */
@Service
@Slf4j
public class CustomerAssistantServiceImpl implements CustomerAssistantService {

    private final WeEmpleCodeChannelMapper weEmpleCodeChannelMapper;
    private final WeEmpleCodeMapper weEmpleCodeMapper;
    private final WeEmpleCodeMaterialService weEmpleCodeMaterialService;
    private final WeCustomerAcquisitionClient weCustomerAcquisitionClient;
    private final WeEmpleCodeUseScopService weEmpleCodeUseScopService;
    private final WeEmpleCodeService weEmpleCodeService;
    private final WeEmpleCodeTagService weEmpleCodeTagService;
    private final WeEmpleCodeSituationService weEmpleCodeSituationService;
    private final WeEmpleCodeChannelService weEmpleCodeChannelService;
    private final WeEmpleCodeAnalyseMapper weEmpleCodeAnalyseMapper;
    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;
    private final CustomerAssistantRedisCache customerAssistantRedisCache;
    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final EmpleStatisticRedisCache empleStatisticRedisCache;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WeCustomerService weCustomerService;
    private final WeUserService weUserService;
    private final WeTagService weTagService;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient weMessagePushClient;

    public CustomerAssistantServiceImpl(WeEmpleCodeChannelMapper weEmpleCodeChannelMapper, WeEmpleCodeMapper weEmpleCodeMapper, WeEmpleCodeMaterialService weEmpleCodeMaterialService, WeCustomerAcquisitionClient weCustomerAcquisitionClient, WeEmpleCodeUseScopService weEmpleCodeUseScopService, WeEmpleCodeService weEmpleCodeService, WeEmpleCodeTagService weEmpleCodeTagService, WeEmpleCodeSituationService weEmpleCodeSituationService, WeEmpleCodeChannelService weEmpleCodeChannelService, WeEmpleCodeAnalyseMapper weEmpleCodeAnalyseMapper, WeFlowerCustomerRelMapper weFlowerCustomerRelMapper, CustomerAssistantRedisCache customerAssistantRedisCache, WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, EmpleStatisticRedisCache empleStatisticRedisCache, WeFlowerCustomerRelService weFlowerCustomerRelService, WeCustomerService weCustomerService, WeUserService weUserService, WeTagService weTagService, WeCustomerTrajectoryService weCustomerTrajectoryService, WeCorpAccountService corpAccountService, WeMessagePushClient weMessagePushClient) {
        this.weEmpleCodeChannelMapper = weEmpleCodeChannelMapper;
        this.weEmpleCodeMapper = weEmpleCodeMapper;
        this.weEmpleCodeMaterialService = weEmpleCodeMaterialService;
        this.weCustomerAcquisitionClient = weCustomerAcquisitionClient;
        this.weEmpleCodeUseScopService = weEmpleCodeUseScopService;
        this.weEmpleCodeService = weEmpleCodeService;
        this.weEmpleCodeTagService = weEmpleCodeTagService;
        this.weEmpleCodeSituationService = weEmpleCodeSituationService;
        this.weEmpleCodeChannelService = weEmpleCodeChannelService;
        this.weEmpleCodeAnalyseMapper = weEmpleCodeAnalyseMapper;
        this.weFlowerCustomerRelMapper = weFlowerCustomerRelMapper;
        this.customerAssistantRedisCache = customerAssistantRedisCache;
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.empleStatisticRedisCache = empleStatisticRedisCache;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
        this.weCustomerService = weCustomerService;
        this.weUserService = weUserService;
        this.weTagService = weTagService;
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
        this.corpAccountService = corpAccountService;
        this.weMessagePushClient = weMessagePushClient;
    }

    /**
     * 新增获客链接
     *
     * @param addWeEmpleCodeDTO {@link AddWeEmpleCodeDTO}
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertCustomerAssistant(AddWeEmpleCodeDTO addWeEmpleCodeDTO) {
        if (addWeEmpleCodeDTO == null || StringUtils.isBlank(addWeEmpleCodeDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 校验参数
        verifyParam(addWeEmpleCodeDTO, addWeEmpleCodeDTO.getIsAutoPass(), addWeEmpleCodeDTO.getIsAutoSetRemark());
        addWeEmpleCodeDTO.setCreateTime(new Date());
        addWeEmpleCodeDTO.setCreateBy(LoginTokenService.getUsername());
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(addWeEmpleCodeDTO.getWelcomeMsgType())) {
            addWeEmpleCodeDTO.buildCodeMsg();
        }
        CustomerAssistantDTO customerAssistantDTO = getCustomerAssistantDTO(addWeEmpleCodeDTO);
        CustomerAssistantResp customerAssistantResp = weCustomerAcquisitionClient.createLink(customerAssistantDTO, addWeEmpleCodeDTO.getCorpId());
        if (customerAssistantResp != null && customerAssistantResp.getLink() != null && StringUtils.isNotBlank(customerAssistantResp.getLink().getUrl())) {
            // 创建默认渠道信息
            WeEmpleCodeChannel weEmpleCodeChannel = new WeEmpleCodeChannel(customerAssistantResp.getLink().getUrl(), CustomerAssistantConstants.DEFAULT_CHANNEL_NAME, addWeEmpleCodeDTO.getId());
            weEmpleCodeChannel.setCreateBy(addWeEmpleCodeDTO.getCreateBy());
            weEmpleCodeChannel.setCreateTime(addWeEmpleCodeDTO.getCreateTime());
            addWeEmpleCodeDTO.setLinkId(customerAssistantResp.getLink().getLink_id());
            addWeEmpleCodeDTO.setQrCode(weEmpleCodeChannel.getChannelUrl());
            // 获客链接格式https://work.weixin.qq.com/ca/XXXXXXXXXX?customer_channel=hk_169388804542XXX,使用”=“号分隔出state的值
            addWeEmpleCodeDTO.setState(weEmpleCodeChannel.getChannelUrl().split(CustomerAssistantConstants.STATE_SPLIT)[1]);
            weEmpleCodeChannelMapper.insert(weEmpleCodeChannel);
        }
        // 保存使用人及部门
        addWeEmpleCodeDTO.getWeEmpleCodeUseScops().forEach(item -> item.setEmpleCodeId(addWeEmpleCodeDTO.getId()));
        weEmpleCodeUseScopService.saveBatch(addWeEmpleCodeDTO.getWeEmpleCodeUseScops());
        // 根据使用人及部门获取userId
        List<String> userIdList = weEmpleCodeService.getUserIdByScope(addWeEmpleCodeDTO.getWeEmpleCodeUseScops(), addWeEmpleCodeDTO.getCorpId());
        // 插入初始化数据
        weEmpleCodeService.handleEmpleStatisticData(userIdList, addWeEmpleCodeDTO.getCorpId(), addWeEmpleCodeDTO.getId());
        // 保存标签信息
        if (CollectionUtils.isNotEmpty(addWeEmpleCodeDTO.getWeEmpleCodeTags())) {
            addWeEmpleCodeDTO.getWeEmpleCodeTags().forEach(item -> item.setEmpleCodeId(addWeEmpleCodeDTO.getId()));
            weEmpleCodeTagService.saveBatch(addWeEmpleCodeDTO.getWeEmpleCodeTags());
        }
        // 构建附件顺序
        weEmpleCodeService.buildMaterialSort(addWeEmpleCodeDTO);
        return weEmpleCodeMapper.insertWeEmpleCode(addWeEmpleCodeDTO);
    }

    /**
     * 修改获客链接
     *
     * @param customerAssistant {@link AddWeEmpleCodeDTO}
     * @return 结果
     */
    @Override
    public Integer updateCustomerAssistant(AddWeEmpleCodeDTO customerAssistant) {
        //校验请求参数
        verifyParam(customerAssistant, customerAssistant.getIsAutoPass(), customerAssistant.getIsAutoSetRemark());
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(customerAssistant.getWelcomeMsgType())) {
            customerAssistant.buildCodeMsg();
        }
        if (customerAssistant.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        customerAssistant.setUpdateTime(new Date());
        customerAssistant.setUpdateBy(LoginTokenService.getUsername());
        List<WeEmpleCodeUseScop> useScops = customerAssistant.getWeEmpleCodeUseScops();
        //更新使用人
        if (CollectionUtils.isNotEmpty(useScops) && useScops.get(0).getBusinessIdType() != null) {
            weEmpleCodeUseScopService.remove(new LambdaUpdateWrapper<WeEmpleCodeUseScop>().eq(WeEmpleCodeUseScop::getEmpleCodeId, customerAssistant.getId()));
            useScops.forEach(item -> item.setEmpleCodeId(customerAssistant.getId()));
            weEmpleCodeUseScopService.saveOrUpdateBatch(useScops);
            // 从作用范围获取userId列表
            List<String> userIdList = weEmpleCodeService.getUserIdByScope(useScops, customerAssistant.getCorpId());
            // 处理活码统计表数据
            weEmpleCodeService.handleEmpleStatisticData(userIdList, customerAssistant.getCorpId(), customerAssistant.getId());
            // 获取获客链接请求参数
            CustomerAssistantDTO customerAssistantDTO = getCustomerAssistantDTO(customerAssistant);
            // 调用企微接口更新
            weCustomerAcquisitionClient.updateLink(customerAssistantDTO, customerAssistant.getCorpId());
        }

        // 更新标签信息
        weEmpleCodeTagService.remove(new LambdaUpdateWrapper<WeEmpleCodeTag>().eq(WeEmpleCodeTag::getEmpleCodeId, customerAssistant.getId()));
        if (CollectionUtils.isNotEmpty(customerAssistant.getWeEmpleCodeTags())) {
            customerAssistant.getWeEmpleCodeTags().forEach(item -> item.setEmpleCodeId(customerAssistant.getId()));
            weEmpleCodeTagService.saveOrUpdateBatch(customerAssistant.getWeEmpleCodeTags());
        }
        weEmpleCodeMaterialService.remove(new LambdaUpdateWrapper<WeEmpleCodeMaterial>().eq(WeEmpleCodeMaterial::getEmpleCodeId, customerAssistant.getId()));
        weEmpleCodeService.buildMaterialSort(customerAssistant);

        // 更新获客链接信息
        return weEmpleCodeMapper.updateWeEmpleCode(customerAssistant);
    }

    /**
     * 查询获客链接
     *
     * @param id     员工活码ID
     * @param corpId 企业Id
     * @return {@link WeEmpleCode}
     */
    @Override
    public WeEmpleCodeVO selectCustomerAssistantById(Long id, String corpId) {
        if (StringUtils.isBlank(corpId) || id == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeEmpleCodeVO weEmpleCodeVO = weEmpleCodeMapper.selectWeEmpleCodeById(id, corpId);
        //组装数据
        weEmpleCodeService.buildEmployCodeMaterial(weEmpleCodeVO, corpId);
        return weEmpleCodeVO;
    }

    /**
     * 获客链接新增回调处理
     *
     * @param state 来源state
     * @param userId 员工id
     * @param externalUserId 客户id
     * @param corpId 企业id
     */
    @Override
    public void callBackAddAssistantHandle(String state, String userId, String externalUserId, String corpId) {
        log.info("[获客链接] 获客链接渠道，新增回调处理开始，state:{},corpId:{},userId:{},externalUserId:{}", state, corpId, userId, externalUserId);
        try {
            // 将"hk_"前缀截取掉，得到渠道id作为state信息
            state = state.replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY);
            // 获取获客链接信息
            SelectWeEmplyCodeWelcomeMsgVO assistantInfo = weEmpleCodeService.selectWelcomeMsgByState(state, corpId);
            // 若不存在，截取state获取渠道id，到获客链接渠道表查询获客链接id
            if (assistantInfo == null) {
                WeEmpleCodeChannel channel = weEmpleCodeChannelMapper.getChannelById(state);
                // 若根据渠道id未查询到信息，则表示不是从获客链接添加的客户，停止处理
                if (channel == null) {
                    log.info("[获客链接] 未查询到该state对应的获客链接信息，state:{},corpId:{},userId:{},externalUserId:{}", state, corpId, userId, externalUserId);
                    return;
                }
                assistantInfo = weEmpleCodeService.selectWelcomeMsgById(String.valueOf(channel.getEmpleCodeId()), corpId);
                // 若渠道已被删除，则将新增的客户计入主渠道中,将state替换为主渠道id
                if (channel.getDelFlag()) {
                    state = weEmpleCodeChannelMapper.getDefaultChannelIdByUrl(assistantInfo.getQrCode());
                }
            }
            if (assistantInfo != null && StringUtils.isNotBlank(assistantInfo.getEmpleCodeId())) {
                String assistantId = assistantInfo.getEmpleCodeId();
                // 根据客户id，获客链接id到分析表查询，是否已经存在数据，若已存在任意与员工、渠道的添加记录关系，则表示当前客户是重复添加，根据获客助手统计规则，只算一次添加。
                List<WeEmpleCodeAnalyse> alreadyRecord = weEmpleCodeAnalyseService.list(new LambdaQueryWrapper<WeEmpleCodeAnalyse>()
                        .eq(WeEmpleCodeAnalyse::getEmpleCodeId, assistantId)
                        .eq(WeEmpleCodeAnalyse::getExternalUserId, externalUserId)
                        .eq(WeEmpleCodeAnalyse::getType, WeEmpleCodeAnalyseTypeEnum.ADD.getType())
                );
                // 若不存在记录，则进行今日新增客户数记录
                if (CollectionUtils.isEmpty(alreadyRecord)) {
                    // 更新获客情况Redis中的数据
                    customerAssistantRedisCache.incrementNewCustomerCnt(corpId, DateUtils.dateTime(new Date()));
                }
                // 更新活码统计Redis中的数据
                empleStatisticRedisCache.addNewCustomerCnt(corpId, DateUtils.dateTime(new Date()), Long.valueOf(assistantId), userId);
                // 更新获客链接统计信息
                weEmpleCodeAnalyseService.saveAssistantAnalyse(corpId, userId, externalUserId, state, assistantId, true);
                //查询外部联系人与通讯录关系数据
                WeFlowerCustomerRel weFlowerCustomerRel = weFlowerCustomerRelService
                        .getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                                .eq(WeFlowerCustomerRel::getUserId, userId)
                                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                                .eq(WeFlowerCustomerRel::getStatus, 0).last(GenConstants.LIMIT_1));
                // 更新获客链接state信息，拼接上之前截取掉的"hk_"前缀
                weFlowerCustomerRel.setState(CustomerAssistantConstants.STATE_PREFIX + state);
                weFlowerCustomerRelService.updateById(weFlowerCustomerRel);
                // 查询外部联系人的信息
                WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getExternalUserid, externalUserId));
                //为外部联系人添加员工活码标签
                setAssistantTag(weFlowerCustomerRel, assistantId, assistantInfo.getTagFlag());
                // 打标签后休眠1S , 避免出现打标签后又打备注提示接口调用频繁 Tower 任务: 客户扫活码加好友之后没有自动备注 ( https://tower.im/teams/636204/todos/69053 )
                ThreadUtil.safeSleep(1000L);
                // 判断是否需要设置备注
                setAssistantRemark(state, userId, externalUserId, corpId, assistantInfo.getRemarkType(), assistantInfo.getRemarkName(), weCustomer.getName());
            }
        } catch (Exception e) {
            log.error("[获客链接] 处理获客链接回调出现异常，corpId:{}, state:{}, 异常原因：{}", corpId, state, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 设置获客链接标签
     *
     * @param weFlowerCustomerRel weFlowerCustomerRel
     * @param assistantId         获客链接主键ID
     * @param tagFlag             是否打标签
     */
    private void setAssistantTag(WeFlowerCustomerRel weFlowerCustomerRel, String assistantId, Boolean tagFlag) {
        if (weFlowerCustomerRel == null || org.apache.commons.lang3.StringUtils.isBlank(assistantId) || tagFlag == null) {
            log.info("[获客链接标签处理] 设置获客链接标签信息不足，assistantId={},tagFlag={},weFlowerCustomerRel={}", assistantId, tagFlag, JSONObject.toJSONString(weFlowerCustomerRel));
            return;
        }
        if (!tagFlag) {
            log.info("[获客链接标签处理] assistantId={} 未开启打标签", assistantId);
            return;
        }
        try {
            // 获取员工详情
            WeUser weUser = weUserService.getUserDetail(weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getUserId());
            if (weUser == null) {
                log.info("[获客链接标签处理] 查询不到员工信息,corpId:{},userId:{}", weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getUserId());
                return;
            }
            // 查询获客链接对应标签
            List<WeEmpleCodeTag> tagList = weEmpleCodeTagService.list(new LambdaQueryWrapper<WeEmpleCodeTag>().eq(WeEmpleCodeTag::getEmpleCodeId, assistantId));
            //存在则打标签
            if (CollectionUtils.isNotEmpty(tagList)) {
                log.info("[获客链接标签处理] 开始批量打标签，被打上的标签信息tagList:{}", tagList);
                // 查询这个tagId对应的groupId
                List<String> tagIdList = tagList.stream().map(WeEmpleCodeTag::getTagId).filter(Objects::nonNull).collect(Collectors.toList());
                weCustomerService.singleMarkLabel(weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getUserId(), weFlowerCustomerRel.getExternalUserid(), tagIdList, weUser.getName());
                // 获取有效的标签名称
                List<String> tagNameList = weTagService.getTagNameByIds(tagIdList);
                // 记录信息动态
                recordAssistantTag(weFlowerCustomerRel.getCorpId(), weUser, weFlowerCustomerRel.getExternalUserid(), tagNameList, assistantId);
            }
        } catch (Exception e) {
            log.error("[获客链接标签处理] 出现异常，assistantId={},e={}", assistantId, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 记录点击获客链接打标签的信息动态
     *
     * @param corpId         公司id
     * @param weUser         员工信息
     * @param externalUserId 客户id
     * @param tagNameList    标签列表
     * @param assistantId    获客链接id
     */
    private void recordAssistantTag(String corpId, WeUser weUser, String externalUserId, List<String> tagNameList, String assistantId) {
        if (StringUtils.isAnyBlank(corpId, externalUserId) || CollUtil.isEmpty(tagNameList) || Objects.isNull(weUser)) {
            log.info("[获客链接信息动态] 打标签的信息动态记录员工，客户id，公司id，标签列表，活码id不能为空，userId：{}，externalUserId：{}，corpId：{}，addTagIds: {}，assistantId:{}", weUser, externalUserId, corpId, tagNameList, assistantId);
            return;
        }
        WeEmpleCode weEmpleCode = weEmpleCodeMapper.selectById(assistantId);
        if (Objects.isNull(weEmpleCode)) {
            log.info("[获客链接信息动态] 查询不到获客链接信息，停止记录信息动态");
            return;
        }
        TagRecordUtil tagRecordUtil = new TagRecordUtil();
        // 根据获客链接名称，员工名称，构建content
        String content = tagRecordUtil.buildAssistantContent(weEmpleCode.getScenario(), weUser.getName());
        // 根据标签名称列表构建标签名称信息
        String detail = String.join(WeConstans.COMMA, tagNameList);
        //保存信息动态
        weCustomerTrajectoryService.saveCustomerTrajectory(corpId, weUser.getUserId(), externalUserId, content, detail);
    }

    /**
     * 给从获客链接加入的客户设置备注
     *
     * @param state          回调state
     * @param userId         员工userId
     * @param externalUserId 客户
     * @param corpId         企业ID
     * @param remarkType     备注类型
     * @param remarkName     设置的备注名
     * @param nickName       客户昵称
     */
    private void setAssistantRemark(String state, String userId, String externalUserId, String corpId,
                                    Integer remarkType, String remarkName, String nickName) {
        if (org.apache.commons.lang3.StringUtils.isBlank(userId)
                || org.apache.commons.lang3.StringUtils.isBlank(externalUserId)
                || org.apache.commons.lang3.StringUtils.isBlank(corpId)
                || remarkType == null) {
            log.error("[setAssistantRemark] 参数异常 state={},userId={},externalUserId={},corpId={},remarkType={}", state, userId, externalUserId, corpId, remarkType);
            return;
        }
        if (WeEmployCodeRemarkTypeEnum.NO.getRemarkType().equals(remarkType) || org.apache.commons.lang3.StringUtils.isBlank(remarkName)) {
            log.info("[setAssistantRemark] remarkType={},remarkName={}", remarkType, remarkName);
            return;
        }
        String newRemark;
        WeCustomer weCustomer = new WeCustomer();
        if (WeEmployCodeRemarkTypeEnum.BEFORT_NICKNAME.getRemarkType().equals(remarkType)) {
            newRemark = remarkName + "-" + nickName;
        } else {
            newRemark = nickName + "-" + remarkName;
        }
        weCustomer.setCorpId(corpId);
        weCustomer.setUserId(userId);
        weCustomer.setExternalUserid(externalUserId);
        weCustomer.setRemark(newRemark);
        try {
            weCustomerService.updateWeCustomerRemark(weCustomer);
        } catch (Exception e) {
            log.error("[setAssistantRemark] error!! corpId={},userId={},externalUserId={},", corpId, userId, externalUserId);
        }
    }

    /**
     * 发送应用通知
     *
     * @param corpId   企业ID
     * @param sendUser 通知员工，多个用"|"隔开
     * @param content  发送内容
     */
    @Override
    public void sendToUser(String corpId, String sendUser, String content) {
        // 发送的应用消息体
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        // 获取企业信息
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        // 获取应用id
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        // 设置发送者 发送给企业员工, 将逗号隔开的员工id换为使用|隔开
        pushDto.setTouser(sendUser);
        // 设置文本消息内容
        text.setContent(content);
        // 设置应用id
        pushDto.setAgentid(Integer.valueOf(agentId));
        // 设置文本消息至消息体
        pushDto.setText(text);
        // 设置发送的消息类型为文本消息
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        weMessagePushClient.sendMessageToUser(pushDto, agentId, corpId);
    }

    /**
     * 获客链接删除回调处理
     *
     * @param state          来源state
     * @param corpId         企业id
     * @param externalUserId 客户id
     * @param userId         员工id
     */
    @Override
    public void callBackDelAssistantHandle(String state, String corpId, String externalUserId, String userId) {
        if (StringUtils.isAnyBlank(state, corpId, externalUserId, userId)) {
            log.info("[获客链接删除回调处理] 参数缺失，停止处理，state:{}，corpId:{}，externalUserId:{}，userId:{}", state, corpId, externalUserId, userId);
            return;
        }
        String channelId = state.replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY);
        WeEmpleCodeChannel channel = weEmpleCodeChannelMapper.getChannelById(channelId);
        if (channel != null) {
            // 根据获客链接id获取获客链接信息
            SelectWeEmplyCodeWelcomeMsgVO messageMap = weEmpleCodeService.selectWelcomeMsgById(String.valueOf(channel.getEmpleCodeId()), corpId);
            // 渠道已被删除
            if (channel.getDelFlag()) {
                // 渠道被删除，将统计数据计入默认渠道
                channelId = weEmpleCodeChannelMapper.getDefaultChannelIdByUrl(messageMap.getQrCode());
            }
            // 更新获客链接活码统计redis中的数据
            empleStatisticRedisCache.addLossCustomerCnt(corpId, DateUtils.dateTime(new Date()), Long.valueOf(messageMap.getEmpleCodeId()), userId);
            // 记录获客链接统计信息
            weEmpleCodeAnalyseService.saveAssistantAnalyse(corpId, userId, externalUserId, channelId, messageMap.getEmpleCodeId(), false);
        }
    }

    /**
     * 获客链接详情-数据总览
     *
     * @param empleCodeId 获客链接id
     * @param corpId 企业ID
     * @return {@link CustomerAssistantDetailTotalVO}
     */
    @Override
    public CustomerAssistantDetailTotalVO detailTotal(String empleCodeId, String corpId) {
        if (StringUtils.isBlank(empleCodeId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        List<Long> channelIdList = weEmpleCodeChannelService.getAssistantChannelNoDel(empleCodeId);
        CustomerAssistantDetailTotalVO assistantDetailTotal = weEmpleCodeAnalyseMapper.getAssistantDetailTotal(channelIdList, corpId, DateUtils.dateTime(new Date()));
        if (assistantDetailTotal == null) {
            return new CustomerAssistantDetailTotalVO();
        }
        // state来源列表
        FindAssistantDetailStatisticCustomerDTO dto = new FindAssistantDetailStatisticCustomerDTO();
        dto.setEmpleCodeId(empleCodeId);
        List<String> stateList = getStateAndSetChannelIdList(dto);
        // 获取数据总览-有效客户数
        CustomerAssistantDetailTotalVO currentNewCustomerCntTotal = weEmpleCodeAnalyseMapper.getAssistantDetailTotalCurrentNewCustomer(stateList, corpId, DateUtils.dateTime(new Date()));
        if (currentNewCustomerCntTotal != null) {
            assistantDetailTotal.setCurrentNewCustomerCnt(currentNewCustomerCntTotal.getCurrentNewCustomerCnt());
        }
        return assistantDetailTotal;
    }

    /**
     * 获客链接详情-数据统计-日期维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return {@link AssistantDetailStatisticDateVO}
     */
    @Override
    public List<AssistantDetailStatisticDateVO> detailStatisticByDate(FindAssistantDetailStatisticCustomerDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // state来源列表
        List<String> stateList = getStateAndSetChannelIdList(dto);
        List<AssistantDetailStatisticDateVO> defaultList = new ArrayList<>();
        // 获取查询日期的范围
        List<Date> dates = DateUtils.findDates(DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getBeginTime()), DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getEndTime()));
        for (Date date : dates) {
            defaultList.add(new AssistantDetailStatisticDateVO(DateUtils.dateTime(date)));
        }
        // 获取日期对应的新增客户数和流失客户数
        List<AssistantDetailStatisticDateVO> detailStatisticByDate = weEmpleCodeAnalyseMapper.getDetailStatisticByDate(dto);
        // 获取日期对应的累计客户数
        List<WeEmpleCodeChannelRelVO> dateAccumulateCntVO = weFlowerCustomerRelMapper.getChannelDateRelAccumulateCnt(dto.getChannelIdList(), dto.getUserIds(), dto.getCorpId(), dto.getEndTime());
        // 获取日期对应的有效客户数
        List<WeEmpleCodeChannelRelVO> channelDateRelEffectCnt = weFlowerCustomerRelMapper.getChannelDateRelEffectCnt(stateList, dto.getUserIds(), dto.getCorpId(), dto.getBeginTime(), dto.getEndTime());
        // 组装数据
        return buildChannelDetailDateData(defaultList, detailStatisticByDate, dateAccumulateCntVO, channelDateRelEffectCnt);
    }

    /**
     * 组装获客链接详情-数据统计-日期维度数据
     *
     * @param defaultList             根据查询日期条件生成的默认数据
     * @param detailStatisticByDate   日期对应的新增客户数和流失客户数列表
     * @param dateAccumulateCntVO     日期对应的累计添加客户数列表
     * @param channelDateRelEffectCnt 日期对应的有效客户数列表
     * @return {@link AssistantDetailStatisticDateVO}
     */
    private List<AssistantDetailStatisticDateVO> buildChannelDetailDateData(List<AssistantDetailStatisticDateVO> defaultList, List<AssistantDetailStatisticDateVO> detailStatisticByDate, List<WeEmpleCodeChannelRelVO> dateAccumulateCntVO, List<WeEmpleCodeChannelRelVO> channelDateRelEffectCnt) {
        if (CollectionUtils.isEmpty(defaultList)) {
            return Collections.emptyList();
        }
        for (AssistantDetailStatisticDateVO dateVO : defaultList) {
            // 不为空，设置日期对应的新增客户数和流失客户数
            if (CollectionUtils.isNotEmpty(detailStatisticByDate)) {
                for (AssistantDetailStatisticDateVO newAndLossCntVO : detailStatisticByDate) {
                    if (dateVO.getDate().equals(newAndLossCntVO.getDate())) {
                        dateVO.setNewCustomerCnt(newAndLossCntVO.getNewCustomerCnt());
                        dateVO.setLossCustomerCnt(newAndLossCntVO.getLossCustomerCnt());
                    }
                }
            }
            // 不为空，设置日期对应的累计客户数
            if (CollectionUtils.isNotEmpty(dateAccumulateCntVO)) {
                for (WeEmpleCodeChannelRelVO accumulateCntVO : dateAccumulateCntVO) {
                    // 累加累计客户数中日期小于或等于当天日期的累计客户数数据
                    if (DateUtils.after(dateVO.getDate(), accumulateCntVO.getDate()) || dateVO.getDate().equals(accumulateCntVO.getDate())) {
                        dateVO.handelAccumulateCnt(accumulateCntVO.getCustomerCnt());
                    }
                }
            }
            // 不为空，设置日期对应的有效客户数
            if (CollectionUtils.isNotEmpty(channelDateRelEffectCnt)) {
                for (WeEmpleCodeChannelRelVO effectCntVO : channelDateRelEffectCnt) {
                    if (dateVO.getDate().equals(effectCntVO.getDate())) {
                        dateVO.setCurrentNewCustomerCnt(effectCntVO.getCustomerCnt());
                    }
                }
            }
        }
        // 根据日期倒序排序
        defaultList.sort(Comparator.comparing(AssistantDetailStatisticDateVO::getDate).reversed());
        return defaultList;
    }

    /**
     * 获取来源列表,设置默认渠道id条件
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return state来源列表
     */
    private List<String> getStateAndSetChannelIdList(FindAssistantDetailStatisticCustomerDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getEmpleCodeId())) {
            return Collections.emptyList();
        }
        // state来源列表
        List<String> stateList = new ArrayList<>();
        // 渠道id列表
        List<Long> channelIdList = new ArrayList<>();
        // 没有选择渠道条件，查询所有的渠道条件
        if(dto.getChannelId() == null) {
            channelIdList = weEmpleCodeChannelMapper.getAllAssistantChannel(dto.getEmpleCodeId());
            dto.setChannelIdList(channelIdList);
            // 将渠道id拼接上"hk_"加入来源state列表
            for (Long channelId : dto.getChannelIdList()) {
                stateList.add(CustomerAssistantConstants.STATE_PREFIX + channelId);
            }
        } else {
            channelIdList.add(dto.getChannelId());
            stateList.add(CustomerAssistantConstants.STATE_PREFIX + dto.getChannelId());
            dto.setChannelIdList(channelIdList);
        }
        return stateList;
    }

    /**
     * 获客链接详情-数据统计-渠道维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return {@link AssistantDetailStatisticChannelVO}
     */
    @Override
    public List<AssistantDetailStatisticChannelVO> detailStatisticByChannel(FindAssistantDetailStatisticCustomerDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // state来源列表
        List<String> stateList = getStateAndSetChannelIdList(dto);
        // 获取该获客链接下的所有渠道id列表
        List<Long> channelIdList = weEmpleCodeChannelService.getAssistantChannelNoDel(dto.getEmpleCodeId());
        PageInfoUtil.startPage();
        List<AssistantDetailStatisticChannelVO> detailStatisticByChannel = weEmpleCodeAnalyseMapper.getDetailStatisticByChannel(dto);
        if (CollectionUtils.isEmpty(detailStatisticByChannel)) {
            return Collections.emptyList();
        }
        // 根据state来源列表查询对应state下的客户数（包括已流失的客户），即所有客户，作为该渠道的累计添加客户数
        List<WeEmpleCodeChannelRelVO> channelRelCntList = weFlowerCustomerRelMapper.getChannelRelAccumulateCnt(channelIdList, dto.getUserIds(), dto.getCorpId(), dto.getEndTime());
        // 根据state来源列表查询对应state下的有效客户数（状态为0、3、4）的客户数量，作为新客留存率的分子
        List<WeEmpleCodeChannelRelVO> channelEffectRelCnt = weFlowerCustomerRelMapper.getChannelRelEffectCnt(stateList, dto.getUserIds(), dto.getCorpId(), dto.getBeginTime(), dto.getEndTime());
        for (AssistantDetailStatisticChannelVO channelVO : detailStatisticByChannel) {
            // 为对应的渠道设置累计客户数
            for (WeEmpleCodeChannelRelVO accumulateVO : channelRelCntList) {
                if (accumulateVO.getState().equals(channelVO.getChannelId())) {
                    channelVO.setAccumulateCustomerCnt(accumulateVO.getCustomerCnt());
                }
            }
            // 为对应的渠道设置新增且未流失的客户数
            for (WeEmpleCodeChannelRelVO effectCntVO : channelEffectRelCnt) {
                if (effectCntVO.getState().replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY).equals(channelVO.getChannelId())) {
                    channelVO.setCurrentNewCustomerCnt(effectCntVO.getCustomerCnt());
                }
            }
        }
        // 默认根据累计添加客户数倒序排序
        detailStatisticByChannel.sort(Comparator.comparing(AssistantDetailStatisticChannelVO::getAccumulateCustomerCnt).reversed());
        return detailStatisticByChannel;
    }

    /**
     * 获客链接详情-数据统计-客户维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return {@link AssistantDetailStatisticCustomerVO}
     */
    @Override
    public List<AssistantDetailStatisticCustomerVO> detailStatisticByCustomer(FindAssistantDetailStatisticCustomerDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 没有选择渠道条件，查询所有的渠道条件
        if(dto.getChannelId() == null) {
            dto.setChannelIdList(weEmpleCodeChannelMapper.getAllAssistantChannel(dto.getEmpleCodeId()));
        }
        PageInfoUtil.startPage();
        List<AssistantDetailStatisticCustomerVO> detailStatisticByCustomer = weEmpleCodeAnalyseMapper.getDetailStatisticByCustomer(dto);
        if (CollectionUtils.isEmpty(detailStatisticByCustomer)) {
            return Collections.emptyList();
        }
        // 获取所有的state列表
        List<String> stateList = detailStatisticByCustomer.stream().map(AssistantDetailStatisticCustomerVO::getState).collect(Collectors.toList());
        // 将所有的state列表截取掉"hk_"部分，就是渠道id列表
        List<String> channelIdList = stateList.stream().map(s -> s.replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY)).collect(Collectors.toList());
        // 根据渠道id，获取渠道信息
        List<WeEmpleCodeChannel> channelList = weEmpleCodeChannelMapper.listChannelByIds(channelIdList);
        for (AssistantDetailStatisticCustomerVO vo : detailStatisticByCustomer) {
            for (WeEmpleCodeChannel channel : channelList) {
                // 判断state是否与渠道id相同，相同则设置渠道信息
                if (vo.getState().replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY).equals(channel.getId().toString())) {
                    vo.setChannelName(channel.getName());
                }
            }
        }
        return detailStatisticByCustomer;
    }

    @Override
    public List<ChannelDetailRangeVO> detailRange(FindChannelRangeChartDTO findChannelRangeChartDTO) {
        if (findChannelRangeChartDTO == null || StringUtils.isBlank(findChannelRangeChartDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        List<ChannelDetailRangeVO> resultList = new ArrayList<>();
        // 查询当前获客链接下的所有渠道id
        findChannelRangeChartDTO.setChannelIdList(weEmpleCodeChannelService.getAssistantChannelNoDel(findChannelRangeChartDTO.getEmpleCodeId()));
        List<ChannelDetailRangeVO> channelRangeList = weEmpleCodeAnalyseMapper.getChannelRangeList(findChannelRangeChartDTO);
        if (CollectionUtils.isNotEmpty(channelRangeList)) {
            resultList = channelRangeList.stream()
                    .filter(channel -> channel.getNewCustomerCnt() != 0)
                    .collect(Collectors.toList());
            // 根据新增客户数倒序排序
            resultList.sort(Comparator.comparing(ChannelDetailRangeVO::getNewCustomerCnt).reversed());
        }
        return resultList;
    }

    /**
     * 导出获客链接详情-数据统计-渠道维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return 结果
     */
    @Override
    public AjaxResult exportDetailStatisticByChannel(FindAssistantDetailStatisticCustomerDTO dto) {
        WeEmpleCode assistant = weEmpleCodeService.getById(dto.getEmpleCodeId());
        // 获客链接不存在
        if (assistant == null) {
            throw new CustomException(ResultTip.TIP_ASSISTANT_NOT_FIND);
        }
        List<AssistantDetailStatisticChannelVO> list = this.detailStatisticByChannel(dto);
        list.forEach(AssistantDetailStatisticChannelVO::bindExportData);
        ExcelUtil<AssistantDetailStatisticChannelVO> utils = new ExcelUtil<>(AssistantDetailStatisticChannelVO.class);
        return utils.exportExcel(list, assistant.getScenario() + CustomerAssistantConstants.EXPORT_CHANNEL_NAME);
    }

    /**
     * 导出获客链接详情-数据统计-日期维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return 结果
     */
    @Override
    public AjaxResult exportDetailStatisticByDate(FindAssistantDetailStatisticCustomerDTO dto) {
        WeEmpleCode assistant = weEmpleCodeService.getById(dto.getEmpleCodeId());
        // 获客链接不存在
        if (assistant == null) {
            throw new CustomException(ResultTip.TIP_ASSISTANT_NOT_FIND);
        }
        List<AssistantDetailStatisticDateVO> list = this.detailStatisticByDate(dto);
        list.forEach(AssistantDetailStatisticDateVO::bindExportData);
        ExcelUtil<AssistantDetailStatisticDateVO> utils = new ExcelUtil<>(AssistantDetailStatisticDateVO.class);
        return utils.exportExcel(list, assistant.getScenario() + CustomerAssistantConstants.EXPORT_DATE_NAME);
    }

    /**
     * 导出获客链接详情-数据统计-客户维度
     *
     * @param dto {@link FindAssistantDetailStatisticCustomerDTO}
     * @return 结果
     */
    @Override
    public AjaxResult exportDetailStatisticByCustomer(FindAssistantDetailStatisticCustomerDTO dto) {
        WeEmpleCode assistant = weEmpleCodeService.getById(dto.getEmpleCodeId());
        // 获客链接不存在
        if (assistant == null) {
            throw new CustomException(ResultTip.TIP_ASSISTANT_NOT_FIND);
        }
        List<AssistantDetailStatisticCustomerVO> list = this.detailStatisticByCustomer(dto);
        ExcelUtil<AssistantDetailStatisticCustomerVO> utils = new ExcelUtil<>(AssistantDetailStatisticCustomerVO.class);
        return utils.exportExcel(list, assistant.getScenario() + CustomerAssistantConstants.EXPORT_CUSTOMER_NAME);
    }

    /**
     * 获客链接详情-趋势图、渠道新增客户数排行
     *
     * @param findChannelRangeChartDTO {@link FindChannelRangeChartDTO}
     * @return {@link ChannelDetailChartVO}
     */
    @Override
    public List<ChannelDetailChartVO> detailChart(FindChannelRangeChartDTO findChannelRangeChartDTO) {
        if (findChannelRangeChartDTO == null || StringUtils.isBlank(findChannelRangeChartDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 若渠道查询条件为空，查询当前获客链接下的所有渠道
        if (StringUtils.isBlank(findChannelRangeChartDTO.getChannelId())) {
            findChannelRangeChartDTO.setChannelIdList(weEmpleCodeChannelService.getAssistantChannelNoDel(findChannelRangeChartDTO.getEmpleCodeId()));
        } else {
            // 渠道id不为空,条件设置为渠道id
            List<Long> channelIdList = new ArrayList<>();
            channelIdList.add(Long.parseLong(findChannelRangeChartDTO.getChannelId()));
            findChannelRangeChartDTO.setChannelIdList(channelIdList);
        }
        // 根据查询日期创建默认日期数据
        List<ChannelDetailChartVO> defaultDateList = new ArrayList<>();
        // 获取查询日期的范围
        List<Date> dates = DateUtils.findDates(DateUtils.dateTime(DateUtils.YYYY_MM_DD, findChannelRangeChartDTO.getBeginTime()), DateUtils.dateTime(DateUtils.YYYY_MM_DD, findChannelRangeChartDTO.getEndTime()));
        // 创建默认数据
        for (Date date : dates) {
            defaultDateList.add(new ChannelDetailChartVO(DateUtils.dateTime(date)));
        }
        // 根据条件获取趋势图数据
        List<ChannelDetailChartVO> channelChartList = weEmpleCodeAnalyseMapper.getChannelChartList(findChannelRangeChartDTO);
        if (CollectionUtils.isNotEmpty(channelChartList)) {
            for (ChannelDetailChartVO chartVO : defaultDateList) {
                for (ChannelDetailChartVO channelDetailChartVO : channelChartList) {
                    if (chartVO.getTime().equals(channelDetailChartVO.getTime())) {
                        chartVO.setNewCustomerCnt(channelDetailChartVO.getNewCustomerCnt());
                        chartVO.setLossCustomerCnt(channelDetailChartVO.getLossCustomerCnt());
                    }
                }
            }
        }
        return defaultDateList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncSituation(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        String today = DateUtils.dateTime(new Date());
        // 获取当前企业下的获客情况
        CustomerAssistantResp.Quota quota = weCustomerAcquisitionClient.quota(corpId);
        if (quota == null) {
            throw new CustomException(ResultTip.TIP_GET_CORP_QUOTA_FAIL);
        }
        // 获取当前企业下的获客情况信息
        WeEmpleCodeSituation situation = weEmpleCodeSituationService.getById(corpId);
        if (situation == null) {
            situation = new WeEmpleCodeSituation(corpId, quota.getTotal(), quota.getBalance());
        } else {
            // 将原缓存中的新增客户数保存到数据库
            situation.handleSyncData(customerAssistantRedisCache.getTodayNewCustomerCnt(corpId, today), quota.getTotal(), quota.getBalance());
        }
        // 同步后，清除缓存中的新增客户数，重新开始记录
        customerAssistantRedisCache.delTodayNewCustomerCnt(corpId, today);
        return weEmpleCodeSituationService.saveOrUpdate(situation) ? 1 : 0;
    }

    /**
     * 获取主页获客情况信息
     *
     * @param corpId 企业ID
     * @return {@link WeEmpleCodeSituationVO}
     */
    @Override
    public WeEmpleCodeSituationVO listSituation(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeEmpleCodeSituation situation = weEmpleCodeSituationService.getById(corpId);
        if (situation == null) {
            return null;
        }
        WeEmpleCodeSituationVO weEmpleCodeSituationVO = new WeEmpleCodeSituationVO();
        BeanUtils.copyProperties(situation, weEmpleCodeSituationVO);
        // 从缓存中获取今日新增客户数
        Integer todayNewCustomerCnt = customerAssistantRedisCache.getTodayNewCustomerCnt(corpId, DateUtils.dateTime(new Date()));
        if (todayNewCustomerCnt != null) {
            // 处理获客情况数据
            weEmpleCodeSituationVO.handleSituationValue(todayNewCustomerCnt);
        }
        return weEmpleCodeSituationVO;
    }

    /**
     * 批量逻辑删除获客链接
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchRemoveCustomerAssistant(String corpId, String ids) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(ids)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 将逗号隔开的id转换为列表
        List<Long> idList = Arrays.stream(com.easyink.common.utils.StringUtils.split(ids, DictUtils.SEPARATOR)).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        List<WeEmpleCode> weEmpleCodes = weEmpleCodeMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(weEmpleCodes)) {
            throw new CustomException(ResultTip.TIP_DELETE_ASSISTANT_LINK_NO_FIND);
        }
        // 获取获客链接id
        List<String> linkIdList = weEmpleCodes.stream().map(WeEmpleCode::getLinkId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(linkIdList)) {
            throw new CustomException(ResultTip.TIP_DELETE_ASSISTANT_LINK_NO_FIND);
        }
        // 发送删除请求到企微
        CustomerAssistantDTO customerAssistantDTO = new CustomerAssistantDTO();
        for (String linkId : linkIdList) {
            customerAssistantDTO.setLink_id(linkId);
            CustomerAssistantResp customerAssistantResp = weCustomerAcquisitionClient.delLink(customerAssistantDTO, corpId);
            if (WeExceptionTip.WE_EXCEPTION_TIP_40058.getCode().equals(customerAssistantResp.getErrcode())) {
                throw new CustomException(WeExceptionTip.WE_EXCEPTION_TIP_40058);
            }
        }
        //删除附件表
        weEmpleCodeMaterialService.removeByEmpleCodeId(idList);
        return weEmpleCodeMapper.batchRemoveWeEmpleCodeIds(corpId, idList);
    }

    /**
     * 获取获客链接请求参数
     *
     * @param dto {@link AddWeEmpleCodeDTO}
     * @return {@link CustomerAssistantDTO}
     */
    private CustomerAssistantDTO getCustomerAssistantDTO(AddWeEmpleCodeDTO dto) {
        if (CollectionUtils.isEmpty(dto.getWeEmpleCodeUseScops()) || StringUtils.isBlank(dto.getCorpId())) {
            return null;
        }
        CustomerAssistantDTO customerAssistant = new CustomerAssistantDTO();
        // 链接名称
        customerAssistant.setLink_name(dto.getScenario());
        // 是否自动通过
        customerAssistant.setSkip_verify(WeEmployCodeSkipVerifyEnum.isPassByNow(dto.getSkipVerify(), dto.getEffectTimeOpen(), dto.getEffectTimeClose()));
        List<String> userIdList = new LinkedList<>();
        List<Long> departmentIdList = new LinkedList<>();
        CustomerAssistantDTO.Range range = new CustomerAssistantDTO.Range();
        dto.getWeEmpleCodeUseScops().forEach(item -> {
            //员工列表
            if (WeConstans.USE_SCOP_BUSINESSID_TYPE_USER.equals(item.getBusinessIdType())
                    && StringUtils.isNotEmpty(item.getBusinessId())) {
                userIdList.add(item.getBusinessId());
            }
            //部门列表
            if (WeConstans.USE_SCOP_BUSINESSID_TYPE_ORG.equals(item.getBusinessIdType())) {
                if (item.getBusinessId() != null) {
                    //查找部门下员工
                    departmentIdList.add(Long.valueOf(item.getBusinessId()));
                }
            }
        });
        // 创建前判断员工数量是否超出API最大限制100个，超出则只截取前100个员工
        String[] userIdArr = userIdList.size() >= CustomerAssistantConstants.MAX_LINK_USER_NUMS ? userIdList.subList(0, CustomerAssistantConstants.MAX_LINK_USER_NUMS).toArray(new String[]{}) : userIdList.toArray(new String[]{});
        range.setUser_list(userIdArr);
        Long[] departmentArr = departmentIdList.toArray(new Long[]{});
        range.setDepartment_list(departmentArr);
        customerAssistant.setRange(range);
        // 若链接id不为空，则设置，用于编辑时传参
        if (StringUtils.isNotBlank(dto.getLinkId())) {
            customerAssistant.setLink_id(dto.getLinkId());
        }
        return customerAssistant;
    }


    /**
     * 新增和修改时 校验请求参数
     *
     * @param weEmpleCode     weEmpleCode
     * @param isAutoPass      是否自动通过
     * @param isAutoSetRemark 是否自动备注
     */
    private void verifyParam(AddWeEmpleCodeDTO weEmpleCode, Boolean isAutoPass, Boolean isAutoSetRemark) {
        if (weEmpleCode == null
                || StringUtils.isBlank(weEmpleCode.getCorpId())
                || weEmpleCode.getSkipVerify() == null
                || StringUtils.isBlank(weEmpleCode.getScenario())
                || weEmpleCode.getRemarkType() == null
                || weEmpleCode.getWeEmpleCodeUseScops() == null
                || weEmpleCode.getWeEmpleCodeUseScops().size() == 0
                || CollectionUtils.isEmpty(weEmpleCode.getWeEmpleCodeUseScops())
                || weEmpleCode.getSource() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //判断兑换码活动中欢迎语是否为空
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            if (ObjectUtils.isEmpty(weEmpleCode.getCodeActivity())) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_ACTIVITY_IS_EMPTY);
            } else {
                if (Long.valueOf(0).equals(weEmpleCode.getCodeActivity().getId())) {
                    throw new CustomException(ResultTip.TIP_REDEEM_CODE_ACTIVITY_IS_EMPTY);
                }
            }
            if (StringUtils.isAllBlank(weEmpleCode.getCodeSuccessMsg(), weEmpleCode.getCodeFailMsg(), weEmpleCode.getCodeRepeatMsg())) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_WELCOME_MSG_IS_EMPTY);
            }
        }

        //当为时间段通过时，需填写开始结束时间
        if (WeEmployCodeSkipVerifyEnum.TIME_PASS.getSkipVerify().equals(weEmpleCode.getSkipVerify())) {
            if (StringUtils.isBlank(weEmpleCode.getEffectTimeOpen()) || StringUtils.isBlank(weEmpleCode.getEffectTimeClose())) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            //开始时间和结束时间不能一致
            if (weEmpleCode.getEffectTimeOpen().equals(weEmpleCode.getEffectTimeClose())) {
                throw new CustomException(ResultTip.TIP_TIME_RANGE_FORMAT_ERROR);
            }
        }
        if (isAutoPass != null && !isAutoPass) {
            weEmpleCode.setSkipVerify(WeEmployCodeSkipVerifyEnum.NO_PASS.getSkipVerify());
        }
        if (isAutoSetRemark != null && !isAutoSetRemark) {
            weEmpleCode.setRemarkType(WeEmployCodeRemarkTypeEnum.NO.getRemarkType());
        }

        //当需要设置客户备注时,remarkName不能为空
        if (!WeEmployCodeRemarkTypeEnum.NO.getRemarkType().equals(weEmpleCode.getRemarkType()) && StringUtils.isBlank(weEmpleCode.getRemarkName())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //数量超出上限抛异常
        if (CollectionUtils.isNotEmpty(weEmpleCode.getMaterialList()) && weEmpleCode.getMaterialList().size() > WeConstans.MAX_ATTACHMENT_NUM) {
            throw new CustomException(ResultTip.TIP_ATTACHMENT_OVER);
        }
    }
}
