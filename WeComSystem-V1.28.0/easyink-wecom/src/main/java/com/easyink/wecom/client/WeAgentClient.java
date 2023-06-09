package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Query;
import com.easyink.wecom.domain.resp.GetAgentResp;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 企业应用API请求客户端接口
 *
 * @author : silver_chariot
 * @date : 2022/7/4 11:29
 **/
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeAgentClient {
    /**
     * 获取指定的应用详情
     *
     * @param agentId 应用ID
     * @return {@link GetAgentResp}
     * @see : https://developer.work.weixin.qq.com/document/path/90227
     */
    // todo 需要接口url 配置到配置文件的 accessToken配置上
    @Get("/agent/get")
    GetAgentResp getAgent(@Query("agentid") String agentId, @Header("corpid") String corpId);



}
