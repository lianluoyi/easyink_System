package com.easyink.wecom.factory;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @description 策略工厂
 * @date 2021/1/20 22:02
 **/
@Service
public class WeStrategyBeanFactory {
    @Autowired
    private final Map<String, WeEventStrategy> eventStrategyMap = new ConcurrentHashMap<>();

    public WeStrategyBeanFactory(Map<String, WeEventStrategy> strategyMap) {
        this.eventStrategyMap.clear();
        strategyMap.forEach((k, v) -> this.eventStrategyMap.put(k, v));
    }

    public void getResource(String type, WxCpXmlMessageVO message) {
        eventStrategyMap.get(type).eventHandle(message);
    }
}
