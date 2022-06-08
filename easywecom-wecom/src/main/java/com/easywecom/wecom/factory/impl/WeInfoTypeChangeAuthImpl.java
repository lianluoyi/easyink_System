package com.easywecom.wecom.factory.impl;

import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeCallBackEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * 类名: WeInfoTypeChangeAuthImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 14:23
 */
@Slf4j
@Service("change_auth")
public class WeInfoTypeChangeAuthImpl implements WeCallBackEventFactory {
    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (ObjectUtils.isEmpty(message)) {
            log.error("message为空");
            return;
        }
        log.info("企业变更授权,suiteId:{},authCorpId:{}", message.getSuiteId(), message.getAuthCorpId());
    }
}
