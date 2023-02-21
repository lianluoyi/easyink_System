package com.easyink.common.constant.wechatopen;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信开放平台常量类
 *
 * @author wx
 * 2023/1/10 14:17
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WechatOpenConstants {

    /**
     * 微信开放平台-三方平台key前缀
     */
    protected static final String WECHAT_OPEN_3RD = "WechatOpen3rd:";

    /**
     * 获取redis缓存的三方平台验证票据key
     *
     * @param componentAppId 三方平台appid
     * @return 验证票据
     */
    public static String getComponentVerifyTicketKey(String componentAppId) {
        return StringUtils.isBlank(componentAppId) ? null : WECHAT_OPEN_3RD + "componentVerifyTicket:" + componentAppId;
    }

    /**
     * 三方平台验证票据过期时间（十分钟）
     */
    public static final Integer COMPONENT_VERIFY_TICKET_EXPIRE_TIME = 60 * 10;

    /**
     * 获取redis缓存的三方平台的ComponentAccessToken的key
     *
     * @param componentAppid        三方平台appid
     * @return key
     */
    public static String getComponentAccessTokenKey(String componentAppid) {
        return WECHAT_OPEN_3RD + "componentAccessToken:" + componentAppid;
    }

    /**
     * 获取redis缓存的三方平台的preAuthCode的key
     *
     * @return key
     */
    public static String getPreAuthCodeKey() {
        return WECHAT_OPEN_3RD + "preAuthCode";
    }

    /**
     * 公众号授权微信三方平台授权页
     */
    protected static final String WECHAT_OPEN_3RD_AUTH_URL =  "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid={component_appid}&pre_auth_code={pre_auth_code}&redirect_uri={domain}/api/wechatopen/getAuthCode?corpId={corpId}&userId={userId}&auth_type=1";

    /**
     * 三方平台appId 占位符
     */
    private static final String COMPONENT_APPID_PLACEHOLDER = "{component_appid}";

    /**
     * 三方平台appId 占位符
     */
    private static final String PRE_AUTH_CODE_PLACEHOLDER = "{pre_auth_code}";

    /**
     * 域名 占位符
     */
    private static final String DOMAIN_PLACEHOLDER = "{domain}";

    /**
     * 企业id 占位符
     */
    private static final String CORP_ID_PLACEHOLDER = "{corpId}";

    /**
     * userId 占位符
     */
    private static final String USER_ID_PLACEHOLDER = "{userId}";

    /**
     * 获取公众号授权微信三方平台授权页
     *
     * @param appId       三方平台appId
     * @param preAuthCode 预授权码
     * @param domain      回调域名
     * @param corpId      企业id
     * @param userId
     * @return 公众号授权微信三方平台授权页url
     */
    public static String getWechatOpen3rdAuthUrl(String appId, String preAuthCode, String domain, String corpId, String userId) {
        if (StringUtils.isAnyBlank(appId, preAuthCode, domain, corpId)) {
            return null;
        }
        return WECHAT_OPEN_3RD_AUTH_URL.replace(COMPONENT_APPID_PLACEHOLDER, appId).replace(PRE_AUTH_CODE_PLACEHOLDER, preAuthCode)
                .replace(DOMAIN_PLACEHOLDER, domain)
                .replace(CORP_ID_PLACEHOLDER, corpId)
                .replace(USER_ID_PLACEHOLDER, userId);
    }
}
