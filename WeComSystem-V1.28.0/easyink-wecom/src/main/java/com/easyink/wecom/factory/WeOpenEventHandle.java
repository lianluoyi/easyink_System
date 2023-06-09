package com.easyink.wecom.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信开放平台回调事件处理类
 *
 * @author wx
 * 2023/1/12 18:18
 **/
@Service
@RequiredArgsConstructor
public class WeOpenEventHandle {
    private final Map<String, WeOpenCallBackEventFactory> weOpenCallBackEventFactoryMap;

    public WeOpenCallBackEventFactory factory(String eventType) {
        if (!weOpenCallBackEventFactoryMap.containsKey(eventType)) {
            return null;
        }
        return weOpenCallBackEventFactoryMap.get(eventType);
    }
}
