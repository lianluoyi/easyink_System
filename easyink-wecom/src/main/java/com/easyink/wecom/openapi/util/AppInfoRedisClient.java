package com.easyink.wecom.openapi.util;


import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.openapi.constant.AppInfoConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 类名: 第三方开发参数redis工具类
 *
 * @author : silver_chariot
 * @date : 2022/3/14 16:31
 */
@Component
public class AppInfoRedisClient extends RedisCache {

    private static final String APP_TICKET_KEY = "appTicket:";
    private static final String APP_NONCE_KEY = "appNonce:";

    /**
     * 获取缓存的ticket
     *
     * @param appId appId
     * @return ticket
     */
    public String getTicket(String appId) {
        if (StringUtils.isBlank(appId)) {
            return StringUtils.EMPTY;
        }
        return getCacheObject(genTicketKey(appId));
    }

    /**
     * 生成缓存ticket的key
     *
     * @param appId appId
     * @return 缓存ticket的key
     */
    public String genTicketKey(String appId) {
        return APP_TICKET_KEY + appId;
    }

    /**
     * 缓存 ticket
     *
     * @param appId  appId
     * @param ticket ticket
     */
    public void setTicket(String appId, String ticket) {
        if (StringUtils.isAnyBlank(appId, ticket)) {
            return;
        }
        setCacheObject(genTicketKey(appId), ticket, AppInfoConst.TICKET_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 获取随机字符串
     *
     * @param appId appId
     */
    public String getNonce(String appId) {
        if (StringUtils.isBlank(appId)) {
            return StringUtils.EMPTY;
        }
        return getCacheObject(genNonceKey(appId));
    }

    /**
     * 设置随机字符串
     *
     * @param appId
     * @param nonce
     */
    public void setNonce(String appId, String nonce) {
        if (StringUtils.isAnyBlank(appId, nonce)) {
            return;
        }
        setCacheObject(genNonceKey(appId), nonce);
    }

    /**
     * 生成缓存随机字符串的key
     *
     * @param appId appId
     * @return 随机字符串的key
     */
    public String genNonceKey(String appId) {
        return APP_NONCE_KEY + appId;
    }

    /**
     * 判断是否是重复的随机字符串
     *
     * @param appId appId
     * @param nonce 随机字符串
     * @return true or false
     */
    public boolean existNonce(String appId, String nonce) {
        if (StringUtils.isAnyBlank(appId, nonce)) {
            return false;
        }
        String lastNonce = getCacheObject(genNonceKey(appId));
        return nonce.equals(lastNonce);
    }
}
