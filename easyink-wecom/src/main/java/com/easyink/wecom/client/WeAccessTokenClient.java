package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.WeAccessTokenDTO;
import com.easyink.wecom.domain.dto.WeAccessUserInfo3rdDTO;
import com.easyink.wecom.domain.dto.WeLoginUserInfoDTO;
import com.easyink.wecom.domain.dto.app.WeSuiteTokenReq;
import com.easyink.wecom.domain.dto.app.WeSuiteTokenResp;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import com.easyink.wecom.interceptor.WeProviderAccessTokenInterceptor;
import com.easyink.wecom.interceptor.WeSuiteAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 获取企业微信Token相关
 *
 * @author: 1*+
 * @date: 2021-08-18 17:01
 */
@Component
@EnableRetry(retryExceptionClass = RetryException.class)
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}")
public interface WeAccessTokenClient {
    /**
     * 获取token(常用token,联系人token)
     *
     * @param corpId     企业ID
     * @param corpSecret 业务Secret
     * @return ${link com.easyink.wecom.domain.dto.WeAccessTokenDTO}
     */
    @Get(url = "/gettoken", interceptor = WeAccessTokenInterceptor.class)
    WeAccessTokenDTO getToken(@Query("corpid") String corpId, @Query("corpsecret") String corpSecret);


    /**
     * 获取供应商token
     *
     * @param corpid         服务商的corpid
     * @param providerSecret 服务商的secret，在服务商管理后台可见
     * @return
     */
    @Post(url = "/service/get_provider_token", interceptor = WeAccessTokenInterceptor.class)
    WeAccessTokenDTO getProviderToken(@Body("corpid") String corpid, @Body("provider_secret") String providerSecret);

    /**
     * 获取第三方应用凭证
     *
     * @return WePreAuthCodeResp
     */
    @Post(url = "/service/get_suite_token", interceptor = WeAccessTokenInterceptor.class)
    WeSuiteTokenResp getSuiteToken(@JSONBody WeSuiteTokenReq weSuiteTokenReq);


    /**
     * 获取登录用户信息(扫码)
     *
     * @param authCode
     * @return
     */
    @Post(url = "/service/get_login_info", interceptor = WeProviderAccessTokenInterceptor.class)
    WeLoginUserInfoDTO getLoginInfo(@Body("auth_code") String authCode);


    /**
     * 获取访问用户身份
     *
     * @param code 授权code
     * @return WeAccessUserInfoDTO
     */
    @Get(url = "/service/getuserinfo3rd", interceptor = WeSuiteAccessTokenInterceptor.class)
    WeAccessUserInfo3rdDTO getUserInfo3rd(@Query("code") String code, @Header("suiteId") String suiteId);

}