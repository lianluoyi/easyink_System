package com.easyink.wecom.handler.shorturl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类名: 短链处理工厂
 *
 * @author : silver_chariot
 * @date : 2023/3/15 14:08
 **/
@Component
@Slf4j
public class ShortUrlHandlerFactory {

    private Map<String, AbstractShortUrlHandler> map = new ConcurrentHashMap<>();

    /**
     * 根据类型获取对应的短链处理类
     *
     * @param type 短链处理类型
     * @return {@link AbstractShortUrlHandler }
     */
    public AbstractShortUrlHandler getByType(Integer type) {
        if (type == null) {
            return null;
        }
        return map.get(type.toString());
    }

    /**
     * 注册到工厂
     *
     * @param type    {@link com.easyink.common.shorturl.enums.ShortUrlTypeEnum}
     * @param handler handler that extends {@link AbstractShortUrlHandler}
     */
    public void register(Integer type, AbstractShortUrlHandler handler) {
        if (type == null || handler == null) {
            return;
        }
        map.put(type.toString(), handler);
    }
}
