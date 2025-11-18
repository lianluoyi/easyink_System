package com.easyink.wecom.factory.impl.customer;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.enums.MessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.model.emplecode.State;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossTagVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.mapper.WeCustomerTempEmpleCodeSettingMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.utils.redis.CustomerRedisCache;
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author admin
 * @description 删除跟进成员事件
 * @date 2021/1/20 23:36
 **/
@Slf4j
@Component("del_follow_user")
public class WeCallBackDelFollowUserImpl extends WeEventStrategy {
    @Autowired
    private WeCustomerService weCustomerService;
    @Autowired
    private WeFlowerCustomerRelService weFlowerCustomerRelService;
    @Autowired
    private WeCorpAccountService weCorpAccountService;
    @Autowired
    private WeMessagePushClient weMessagePushClient;
    @Autowired
    private WeSensitiveActHitService weSensitiveActHitService;
    @Autowired
    private WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    @Autowired
    private WeCustomerTrajectoryService weCustomerTrajectoryService;
    @Autowired
    private WeLossTagService weLossTagService;
    @Autowired
    private WeUserMapper weUserMapper;

    @Autowired
    private WeTagService weTagService;
    @Autowired
    private CustomerRedisCache customerRedisCache;

    private final EmpleStatisticRedisCache empleStatisticRedisCache;
    private final CustomerAssistantService customerAssistantService;
    private final WeCustomerTempEmpleCodeSettingMapper weCustomerTempEmpleCodeSettingMapper;

    public WeCallBackDelFollowUserImpl(EmpleStatisticRedisCache empleStatisticRedisCache,
                                       CustomerAssistantService customerAssistantService,
                                       WeCustomerTempEmpleCodeSettingMapper weCustomerTempEmpleCodeSettingMapper
                                       ) {
        this.empleStatisticRedisCache = empleStatisticRedisCache;
        this.customerAssistantService = customerAssistantService;
        this.weCustomerTempEmpleCodeSettingMapper = weCustomerTempEmpleCodeSettingMapper;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null
                || StringUtils.isBlank(message.getToUserName())
                || message.getUserId() == null
                || message.getExternalUserId() == null) {
            log.error("del_follow_user:返回数据不完整,message:{}", message);
            return;
        }
        // 存入缓存列表 稍后处理
        customerRedisCache.saveCallback(message.getToUserName(), message.getUserId(), message.getExternalUserId(), message);

    }

    /**
     * 处理流失客户回调
     *
     * @param message 回调消息
     */
    public void handleDelFollowUser(WxCpXmlMessageVO message) {
        String corpId = message.getToUserName();
        try {
            weFlowerCustomerRelService.deleteFollowUser(message.getUserId(), message.getExternalUserId(), Constants.DELETE_CODES, corpId);

            WeCorpAccount validWeCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
            Optional.ofNullable(validWeCorpAccount).ifPresent(weCorpAccount -> {
                String customerChurnNoticeSwitch = weCorpAccount.getCustomerChurnNoticeSwitch();
                String customerLossTagSwitch = weCorpAccount.getCustomerLossTagSwitch();
                // 流失提醒
                if (WeConstans.DEL_FOLLOW_USER_SWITCH_OPEN.equals(customerChurnNoticeSwitch)) {
                    WeCustomer weCustomer = weCustomerService.selectWeCustomerById(message.getExternalUserId(), corpId);
                    if (weCustomer == null) {
                        return;
                    }
                    String content = "您已经被客户@" + weCustomer.getName() + "删除!";
                    TextMessageDTO textMessageDTO = new TextMessageDTO();
                    textMessageDTO.setContent(content);
                    WeMessagePushDTO weMessagePushDto = new WeMessagePushDTO();
                    weMessagePushDto.setMsgtype(MessageType.TEXT.getMessageType());
                    weMessagePushDto.setTouser(message.getUserId());
                    weMessagePushDto.setText(textMessageDTO);
                    Optional.ofNullable(validWeCorpAccount)
                            .map(WeCorpAccount::getAgentId)
                            .ifPresent(agentId -> weMessagePushDto.setAgentid(Integer.valueOf(agentId)));
                    weMessagePushClient.sendMessageToUser(weMessagePushDto, weMessagePushDto.getAgentid()
                                                                                            .toString(), corpId);

                }
                // 打客户流失标签
                if (WeConstans.DEL_FOLLOW_USER_SWITCH_OPEN.equals(customerLossTagSwitch)) {
                    addLossTag(message, corpId);
                }
            });
            // 更新活码统计
            WeFlowerCustomerRel weFlowerCustomerRel = weFlowerCustomerRelService.getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                    .eq(WeFlowerCustomerRel::getCorpId, corpId)
                    .eq(WeFlowerCustomerRel::getUserId, message.getUserId())
                    .eq(WeFlowerCustomerRel::getExternalUserid, message.getExternalUserId())
                    .last(GenConstants.LIMIT_1)
            );
            // state 为空，表示不是从活码/获客链接加的外部联系人
            if (weFlowerCustomerRel != null && StringUtils.isNotBlank(weFlowerCustomerRel.getState())) {
                State state = State.valueOf(weFlowerCustomerRel.getState());
                if (state.isAssistantState()) {
                    // 获客链接添加的外部联系人删除处理
                    customerAssistantService.callBackDelAssistantHandle(weFlowerCustomerRel.getState(), corpId, message.getExternalUserId(), message.getUserId());
                } else {
                    State stateObj = State.valueOf(weFlowerCustomerRel.getState());
                    Long originEmplyId = null;
                    if (stateObj.isCustomerEmploy()) {
                        Long customerEmployCodeId = stateObj.unWrapCustomerEmployCodeId();
                        originEmplyId = weCustomerTempEmpleCodeSettingMapper.selectOriginalEmpleCodeIdByCustomerEmployCodeIdIncludeDeleteFlag(customerEmployCodeId);
                        //　兜底
                        if (originEmplyId == null) {
                            log.info("专属活码获取原活码失败, customerEmployCodeId: {}", customerEmployCodeId);
                            originEmplyId = customerEmployCodeId;
                        }
                    } else {
                        originEmplyId = Long.valueOf(stateObj.getState());
                    }
                    // 员工活码添加的外部联系人处理
                    weEmpleCodeAnalyseService.saveWeEmpleCodeAnalyse(corpId, message.getUserId(), message.getExternalUserId(), String.valueOf(originEmplyId), false);
                    // 更新Redis中的数据
                    empleStatisticRedisCache.addLossCustomerCnt(corpId, DateUtils.dateTime(new Date()), originEmplyId, message.getUserId());
                }
            }
            // 客户轨迹:记录删除跟进成员事件
            weCustomerTrajectoryService.saveActivityRecord(corpId, message.getUserId(), message.getExternalUserId(),
                    CustomerTrajectoryEnums.SubType.DEL_USER.getType());
        } catch (Exception e) {
            log.error("del_follow_user>>>>>>>>>>>>>param:{},ex:{}", JSON.toJSONString(message), ExceptionUtils.getStackTrace(e));
        }
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
     * 为流失客户打标签
     *
     * @param message 回调信息
     * @param corpId  企业ID
     */
    private void addLossTag(WxCpXmlMessageVO message, String corpId) {
        CustomerLossTagVO customerLossTagVO = weLossTagService.selectLossWeTag(corpId);
        if (customerLossTagVO == null) {
            throw new CustomException(ResultTip.TIP_FAIL_ADD_LOSS_TAG);
        }
        List<String> lossTagIdList = new ArrayList<>();
        customerLossTagVO.getWeTags().forEach(item -> lossTagIdList.add(item.getTagId()));
        weCustomerService.singleMarkLabel(corpId, message.getUserId(), message.getExternalUserId(), lossTagIdList, null);
        recordLossTag(corpId,message.getUserId(),message.getExternalUserId(),lossTagIdList);
    }

    /**
     * 记录为流失客户打标签信息到侧边栏信息动态中
     *
     * @param corpId 公司id
     * @param userId 员工id
     * @param externalUserId 客户id
     * @param addTagIds 标签id列表
     */
    public void recordLossTag(String corpId, String userId, String externalUserId, List<String> addTagIds){
        if (StringUtils.isAnyBlank(corpId, userId, externalUserId) || CollUtil.isEmpty(addTagIds)) {
            log.info("记录流失客户打标签信息动态时,员工id，客户id，公司id不能为空，userId：{}，externalUserId：{}，corpId：{}", userId, externalUserId, corpId);
            return;
        }
        TagRecordUtil tagRecordUtil=new TagRecordUtil();
        WeUserVO weUserVO=weUserMapper.getUser(corpId,userId);
        if (Objects.isNull(weUserVO)){
            log.info("记录流失客户打标签信息动态时,查询员工详细异常,userId：{}，corpId：{}",userId,corpId);
            return;
        }
        String content = tagRecordUtil.buildLossTagContent(weUserVO.getUserName());
        // 获取有效的标签名称
        List<String> tagName = weTagService.getTagNameByIds(addTagIds);
        if (CollectionUtils.isEmpty(tagName)){
            log.info("记录流失客户打标签信息动态时,查询标签异常");
            return;
        }
        String detail = String.join(DictUtils.SEPARATOR, tagName);
        //保存信息动态
        weCustomerTrajectoryService.saveCustomerTrajectory(corpId,userId,externalUserId,content,detail);
    }
}
