package com.easywecom.wecom.factory.impl;

import com.alibaba.fastjson.JSON;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeCallBackEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 类名: WeEventSubscribeImpl
 *
 * @author: 1*+
 * @date: 2021-12-29 13:56
 */
@Service("unsubscribe")
@Slf4j
public class WeEventUnSubscribeImpl implements WeCallBackEventFactory {


    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        log.info("取消订阅消息:{}", JSON.toJSONString(message));
    }
}
