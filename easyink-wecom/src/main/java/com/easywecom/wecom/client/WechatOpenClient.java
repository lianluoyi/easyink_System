package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easywecom.wecom.domain.req.GenerateUrlLinkReq;
import com.easywecom.wecom.domain.resp.*;
import com.easywecom.wecom.interceptor.WechatOpenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 公众号请求客户端
 *
 * @author : silver_chariot
 * @date : 2022/7/20 10:38
 **/
@Component
@BaseRequest(baseURL = "https://api.weixin.qq.com", interceptor = WechatOpenInterceptor.class)
public interface WechatOpenClient {
    /**
     * 【公众号】获取用户授权信息
     *
     * @param appId     公众号appid
     * @param secret    公众号secret
     * @param code      前端传来的code
     * @param grantType 默认值  authorization_code
     * @param corpId
     * @return {@link GetOfficialAuthInfoResp}
     */
    @Get("/sns/oauth2/access_token")
    GetOfficialAuthInfoResp getAuthInfo(@Query("appid") String appId, @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grantType,@Header("corpId") String corpId);





    /**
     * 【小程序】生成小程序的跳转链接
     *
     * @param req 请求{@link GenerateUrlLinkReq}
     * @return {@link GenerateUrlLinkResp }
     */
    @Post("/wxa/generate_urllink")
    GenerateUrlLinkResp generateUrlLink(@Body GenerateUrlLinkReq req);

    /**
     * 【小程序】 获取accessToken
     *
     * @param grant_type 填写 client_credential
     * @param appid      小程序唯一凭证，即 AppID，可在「微信公众平台 - 设置 - 开发设置」页中获得。（需要已经成为开发者，且帐号没有异常状态）
     * @param secret     小程序唯一凭证密钥，即 AppSecret，获取方式同 appid
     * @return {@link GetAccessTokenResp}
     */

    @Get("/cgi-bin/token")
    GetAccessTokenResp getAccessToken(@Query("grant_type") String grant_type,
                                      @Query("appid") String appid,
                                      @Query("secret") String secret);

    /**
     * 【公众号】获取unionId详情
     *
     * @param openid 用户公众号openid
     * @param lang   语言 国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return {@link GetUnionUserInfoResp }
     */
    @Get("/cgi-bin/user/info")
    GetUnionUserInfoResp getUnionUserInfo(@Query("openid") String openid, @Query("lang") String lang);

    /**
     * 网页链接获取用户信息 （unionid )_
     *
     * @param accessToken accessToken 由接口 {@link WechatOpenClient#getAccessToken(String, String, String) 返回的accessToken 与全局的accessToken不同 }
     * @param openId      openId
     * @param lang        语言
     * @return {@link SnsUserInfoResp }
     */
    @Get("/sns/userinfo")
    SnsUserInfoResp snsUserInfo(@Query("access_token") String accessToken,
                                @Query("openid") String openId,
                                @Query("lang") String lang);

}
