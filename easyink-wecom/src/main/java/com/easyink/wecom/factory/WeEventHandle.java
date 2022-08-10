package com.easyink.wecom.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 类名： WeEventHandle
 *
 * @author 佚名
 * @date 2021/8/26 20:23
 */
@Service
public class WeEventHandle {
    @Autowired
    private Map<String, WeCallBackEventFactory> weCallBackEventFactoryMap;

    public WeCallBackEventFactory factory(String eventType) {
        if (!weCallBackEventFactoryMap.containsKey(eventType)) {
            return null;
        }
        return weCallBackEventFactoryMap.get(eventType);
    }
}
