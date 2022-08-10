package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easywecom.wecom.domain.dto.WeAccessTokenDTO;
import com.easywecom.wecom.domain.dto.app.WeAdminListResp;
import com.easywecom.wecom.domain.dto.app.WePermanentCodeResp;
import com.easywecom.wecom.domain.dto.app.WePreAuthCodeResp;
import com.easywecom.wecom.interceptor.WeSuiteAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: We3rdAppClient
 *
 * @author: 1*+
 * @date: 2021-09-08 16:39
 */

@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeSuiteAccessTokenInterceptor.class)
public interface We3rdAppClient {


    /**
     * 获取预授权码
     *
     * @return WePreAuthCodeResp
     */
    @Get(url = "/service/get_pre_auth_code")
    WePreAuthCodeResp getPreAuthCode(@Header("suiteId") String suiteId);

    /**
     * 获取永久授权码
     *
     * @param authCode 授权码
     * @return {@link WePermanentCodeResp}
     */
    @Post(url = "/service/get_permanent_code")
    WePermanentCodeResp getPermanentCodeInfo(@Body("auth_code") String authCode, @Header("suiteId") String suiteId);


    /**
     * 获取企业凭证
     *
     * @param authCorpid    授权方corpid
     * @param permanentCode 永久授权码
     * @return {@link WeAccessTokenDTO}
     */
    @Post(url = "/service/get_corp_token")
    WeAccessTokenDTO getCorpToken(@Body("auth_corpid") String authCorpid, @Body("permanent_code") String permanentCode, @Header("suiteId") String suiteId);


    /**
     * 获取应用的管理员列表
     *
     * @param authCorpid 授权方corpid
     * @param agentid    授权方安装的应用agentid
     * @return {@link WeAdminListResp}
     */
    @Post(url = "/service/get_admin_list")
    WeAdminListResp getAdminList(@Body("auth_corpid") String authCorpid, @Body("agentid") String agentid, @Header("suiteId") String suiteId);


}
