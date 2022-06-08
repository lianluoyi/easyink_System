package com.easywecom.wecom.factory.impl.customer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.constant.Constants;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.enums.CustomerTrajectoryEnums;
import com.easywecom.common.enums.MessageType;
import com.easywecom.common.enums.WeSensitiveActEnum;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeFlowerCustomerRel;
import com.easywecom.wecom.domain.WeSensitiveAct;
import com.easywecom.wecom.domain.WeSensitiveActHit;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.message.TextMessageDTO;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

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

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null
                || StringUtils.isBlank(message.getToUserName())
                || message.getUserId() == null
                || message.getExternalUserId() == null) {
            log.error("del_follow_user:返回数据不完整,message:{}", message);
            return;
        }
        String corpId = message.getToUserName();
        try {
            weFlowerCustomerRelService.deleteFollowUser(message.getUserId(), message.getExternalUserId(), Constants.DELETE_CODES, corpId);

            WeCorpAccount validWeCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
            Optional.ofNullable(validWeCorpAccount).ifPresent(weCorpAccount -> {
                String customerChurnNoticeSwitch = weCorpAccount.getCustomerChurnNoticeSwitch();
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
                    Optional.ofNullable(validWeCorpAccount).map(WeCorpAccount::getAgentId).ifPresent(agentId -> weMessagePushDto.setAgentid(Integer.valueOf(agentId)));
                    weMessagePushClient.sendMessageToUser(weMessagePushDto, weMessagePushDto.getAgentid().toString(), corpId);

                }
            });
            // 更新活码统计
            WeFlowerCustomerRel weFlowerCustomerRel = weFlowerCustomerRelService.getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                    .eq(WeFlowerCustomerRel::getCorpId, corpId)
                    .eq(WeFlowerCustomerRel::getUserId, message.getUserId())
                    .eq(WeFlowerCustomerRel::getExternalUserid, message.getExternalUserId())
                    .last(GenConstants.LIMIT_1)
            );
            weEmpleCodeAnalyseService.saveWeEmpleCodeAnalyse(corpId, message.getUserId(), message.getExternalUserId(), weFlowerCustomerRel.getState(), false);
            // 客户轨迹:记录删除跟进成员事件
            weCustomerTrajectoryService.saveActivityRecord(corpId, message.getUserId(), message.getExternalUserId(),
                    CustomerTrajectoryEnums.SubType.DEL_USER.getType());
        } catch (Exception e) {
            log.error("del_follow_user>>>>>>>>>>>>>param:{},ex:{}", JSON.toJSONString(message), ExceptionUtils.getStackTrace(e));
        }
    }
}
