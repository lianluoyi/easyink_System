package com.easyink.wecom.factory.impl.customer;

import com.easyink.common.constant.WeConstans;
import com.easyink.wecom.domain.WeChatContactMapping;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeChatContactMappingService;
import com.easyink.wecom.service.WeCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 客户同意进行聊天内容存档事件回调
 * @date 2021/1/21 1:24
 **/
@Slf4j
@Component("msg_audit_approved")
public class WeCallBackMsgAuditApprovedImpl extends WeEventStrategy {
    @Autowired
    private WeCustomerService weCustomerService;
    @Autowired
    private WeChatContactMappingService weChatContactMappingService;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(message.getAuthCorpId())) {
            log.error("{}:企业id不能为空", message.getChangeType());
            return;
        }
        String userId = message.getUserId();
        String externalUserId = message.getExternalUserId();
        String corpId = message.getToUserName();
        WeChatContactMapping fromMapping = new WeChatContactMapping();
        fromMapping.setFromId(userId);
        fromMapping.setReceiveId(externalUserId);
        fromMapping.setIsCustom(WeConstans.ID_TYPE_EX);
        fromMapping.setCorpId(corpId);
        weChatContactMappingService.insertWeChatContactMapping(fromMapping);
        WeChatContactMapping receiveMapping = new WeChatContactMapping();
        receiveMapping.setFromId(externalUserId);
        receiveMapping.setReceiveId(userId);
        receiveMapping.setIsCustom(WeConstans.ID_TYPE_USER);
        receiveMapping.setCorpId(corpId);
        weChatContactMappingService.insertWeChatContactMapping(receiveMapping);

        weCustomerService.saveOrUpdate(WeCustomer.builder()
                .corpId(message.getAuthCorpId())
                .externalUserid(externalUserId)
                .isOpenChat(WeConstans.OPEN_CHAT)
                .userId(userId)
                .build());
    }
}