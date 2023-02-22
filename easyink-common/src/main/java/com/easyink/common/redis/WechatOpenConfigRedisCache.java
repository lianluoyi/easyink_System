package com.easyink.common.redis;

import com.easyink.common.core.redis.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.easyink.common.constant.wechatopen.WechatOpenConstants.*;

/**
 * 微信公众号设置redis缓存
 *
 * @author wx
 * 2023/1/10 14:03
 **/
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component("wechatOpenConfigRedisCache")
public class WechatOpenConfigRedisCache extends RedisCache {

    /**
     * 设置三方平台验证票据
     *
     * @param componentAppid        三方平台appid
     * @param componentVerifyTicket 票据
     */
    public void setComponentVerifyTicket(String componentAppid, String componentVerifyTicket) {
        if (StringUtils.isAnyBlank(componentAppid, componentVerifyTicket)) {
            return;
        }
        String componentVerifyTicketKey = getComponentVerifyTicketKey(componentAppid);
        setCacheObject(componentVerifyTicketKey, componentVerifyTicket, COMPONENT_VERIFY_TICKET_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 获取三方平台验证票据
     *
     * @param componentAppid    三方平台appid
     * @return  componentVerifyTicket
     */
    public String getComponentVerifyTicket(String componentAppid){
        if (StringUtils.isBlank(componentAppid)) {
            return null;
        }
        String componentVerifyTicketKey = getComponentVerifyTicketKey(componentAppid);
        return StringUtils.isBlank(componentVerifyTicketKey) ? null : getCacheObject(componentVerifyTicketKey);
    }

    /**
     * 设置三方平台componentAccessToken
     *
     * @param componentAppid            三方平台appid
     * @param componentAccessToken      三方平台componentAccessToken
     * @param expiresIn                 过期时间 单位秒
     */
    public void setComponentAccessToken(String componentAppid, String componentAccessToken, Integer expiresIn) {
        if (StringUtils.isAnyBlank(componentAppid, componentAccessToken) || expiresIn == null) {
            return;
        }
        String componentAccessTokenKey = getComponentAccessTokenKey(componentAppid);
        setCacheObject(componentAccessTokenKey, componentAccessToken, expiresIn, TimeUnit.SECONDS);
    }

    /**
     * 获取三方平台componentAccessToken
     *
     * @param componentAppid        三方平台appid
     * @return 三方平台componentAccessToken
     */
    public String getComponentAccessToken(String componentAppid) {
        if (StringUtils.isBlank(componentAppid)) {
            return null;
        }
        String componentAccessTokenKey = getComponentAccessTokenKey(componentAppid);
        return getCacheObject(componentAccessTokenKey);
    }

    /**
     * 设置三方平台componentAccessToken
     *
     * @param preAuthCode               三方平台预授权码
     * @param expiresIn                 过期时间 单位秒
     */
    public void setPreAuthCode(String preAuthCode, Integer expiresIn) {
        if (StringUtils.isBlank(preAuthCode)|| expiresIn == null) {
            return;
        }
        String preAuthCodeKey = getPreAuthCodeKey();
        setCacheObject(preAuthCodeKey, preAuthCode, expiresIn, TimeUnit.SECONDS);
    }

    /**
     * 获取三方平台preAuthCode
     *
     * @return  preAuthCode
     */
    public String getPreAuthCode() {
        String preAuthCodeKey = getPreAuthCodeKey();
        return getCacheObject(preAuthCodeKey);
    }

}
