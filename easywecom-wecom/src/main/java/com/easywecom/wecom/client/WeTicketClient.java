package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Query;
import com.easywecom.wecom.domain.WeH5TicketDto;
import com.easywecom.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: h5获取签名
 *
 * @author: 1*+
 * @date: 2021-08-18 17:12
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeTicketClient {


    /**
     * 获取企业的jsapi_ticket
     *
     * @param agentId 应用ID
     * @param corpId  企业ID
     * @return {@link WeH5TicketDto}
     */
    @Get(url = "/get_jsapi_ticket")
    WeH5TicketDto getJsapiTicket(@Header("agentId") String agentId, @Header("corpid") String corpId);

    /**
     * 获取应用的jsapi_ticket
     *
     * @param type 默认值：agent_config
     * @param agentId 应用ID
     * @param corpId 企业ID
     * @return {@link WeH5TicketDto}
     */
    @Get(url = "/ticket/get")
    WeH5TicketDto getTicket(@Query("type") String type, @Header("agentId") String agentId, @Header("corpid") String corpId);
}
