package com.easyink.wecom.factory.impl.customer;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * 类名: 客户编辑事件回调处理
 *
 * @author: 1*+
 * @date: 2021-08-04 14:02
 */
@Slf4j
@Component("edit_external_contact")
public class WeCallBackEditExternalContactImpl extends WeEventStrategy {

    private final WeCustomerService weCustomerService;

    @Autowired
    public WeCallBackEditExternalContactImpl(@NotNull WeCustomerService weCustomerService) {
        this.weCustomerService = weCustomerService;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getExternalUserId(), message.getUserId(), message.getToUserName())) {
            log.error("edit_external_contact:回调消息不完整,message:{}" , message);
            return;
        }
        weCustomerService.updateExternalContactV2(message.getToUserName(), message.getUserId(), message.getExternalUserId());
    }
}
