package com.easyink.wecom.factory.impl;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.factory.WeStrategyBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 获客链接回调事件处理类
 *
 * @author lichaoyu
 * @date 2023/8/29 21:24
 */
@Service("customer_acquisition")
@Slf4j
public class WeEventCustomerAcquisitionImpl implements WeCallBackEventFactory {

    private final WeStrategyBeanFactory weStrategyBeanFactory;
    private final RuoYiConfig ruoYiConfig;

    @Autowired
    public WeEventCustomerAcquisitionImpl(WeStrategyBeanFactory weStrategyBeanFactory, RuoYiConfig ruoYiConfig) {
        this.weStrategyBeanFactory = weStrategyBeanFactory;
        this.ruoYiConfig = ruoYiConfig;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (StringUtils.isNotBlank(message.getSuiteId()) && message.getSuiteId().equals(ruoYiConfig.getProvider().getWebSuite().getSuiteId())) {
            //三方应用的回调客户群通知不处理
            return;
        }
        String changeType = message.getChangeType();
        log.info("回调客户事件通知,通知类型：{},企业ID：{}", changeType, message.getToUserName());
        weStrategyBeanFactory.getResource(changeType, message);
    }
}
