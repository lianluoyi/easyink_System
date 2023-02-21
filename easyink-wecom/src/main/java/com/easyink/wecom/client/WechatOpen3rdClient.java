package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.easyink.wecom.domain.resp.WechatOpen3rdResp;
import com.easyink.wecom.interceptor.WechatOpen3rdInterceptor;
import org.springframework.stereotype.Component;

/**
 * 微信开放平台-第三方平台Client
 * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1453779503&token=&lang=zh_CN
 *
 * @author wx
 * 2023/1/10 9:52
 **/
@Component
@BaseRequest(baseURL = "${wxServerUrl}${wxServerPrefix}", interceptor = WechatOpen3rdInterceptor.class, contentType = "application/json")
public interface WechatOpen3rdClient {


    String JSON = "json";

    /**
     * 获取第三方平台component_access_token
     *
     *  第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。
     *  每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新。
     *
     * @param componentAppSecret      第三方平台appsecret
     * @param componentVerifyTicket   微信后台推送的ticket，此ticket会定时推送
     * @return {@link WechatOpen3rdResp.ComponentAccessToken}
     */
    @Post(value = "/component/api_component_token", dataType = JSON)
    WechatOpen3rdResp.ComponentAccessToken getComponentAccessToken(@Body("component_appsecret") String componentAppSecret, @Body("component_verify_ticket") String componentVerifyTicket);

    /**
     * 获取预授权码pre_auth_code
     *
     *  该API用于获取预授权码。预授权码用于公众号或小程序授权时的第三方平台方安全验证。
     *
     * @return {@link WechatOpen3rdResp.PreAuthCode}
     */
    @Post(value = "/component/api_create_preauthcode")
    WechatOpen3rdResp.PreAuthCode getPreAuthCode();

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     *
     * 该API用于使用授权码换取授权公众号或小程序的授权信息，并换取authorizer_access_token和authorizer_refresh_token。
     * 授权码的获取，需要在用户在第三方平台授权页中完成授权流程后，在回调URI中通过URL参数提供给第三方平台方
     *
     * @param authorizationCode  授权code,会在授权成功时返回给第三方平台
     * @return
     */
    @Post(value = "/component/api_query_auth")
    WechatOpen3rdResp getQueryAuth(@Body("authorization_code") String authorizationCode);

    /**
     *  获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
     *  该API用于在授权方令牌（authorizer_access_token）失效时，可用刷新令牌（authorizer_refresh_token）获取新的令牌
     *
     * @param authorizerAppid          授权方appid
     * @param authorizerRefreshToken   授权方的刷新令牌，刷新令牌主要用于第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，
     * @return {@link WechatOpen3rdResp.AuthorizerToken}
     */
    @Post(value = "/component/api_authorizer_token")
    WechatOpen3rdResp.AuthorizerToken getRefreshToken(@Body("authorizer_appid") String authorizerAppid, @Body("authorizer_refresh_token") String authorizerRefreshToken);


    /**
     * 获取授权方的帐号基本信息
     * 该API用于获取授权方的基本信息，包括头像、昵称、帐号类型、认证类型、微信号、原始ID和二维码图片URL。
     *
     * @param authorizerAppid    授权方appid
     * @return  {@link WechatOpen3rdResp#authorizer_info}
     */
    @Post(value = "/component/api_get_authorizer_info")
    WechatOpen3rdResp getAuthorizerInfo(@Body("authorizer_appid") String authorizerAppid);


}
