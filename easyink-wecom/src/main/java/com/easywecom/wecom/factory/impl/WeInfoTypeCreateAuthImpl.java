package com.easywecom.wecom.factory.impl;

import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeCallBackEventFactory;
import com.easywecom.wecom.service.We3rdAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名: WeInfoTypeCreateAuthImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 14:23
 */
@Slf4j
@Service("create_auth")
public class WeInfoTypeCreateAuthImpl implements WeCallBackEventFactory {

    private final We3rdAppService we3rdAppService;

    @Autowired
    public WeInfoTypeCreateAuthImpl(We3rdAppService we3rdAppService) {
        this.we3rdAppService = we3rdAppService;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (ObjectUtils.isEmpty(message)) {
            log.error("message为空");
            return;
        }
        if (StringUtils.isNotBlank(message.getAuthCode())) {
            we3rdAppService.handlePermanentCode(message.getAuthCode(), message.getSuiteId());
        }
    }
}
