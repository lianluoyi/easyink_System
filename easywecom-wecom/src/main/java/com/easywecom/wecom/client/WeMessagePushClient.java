package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.WeMessagePushGroupDTO;
import com.easywecom.wecom.domain.dto.WeMessagePushResultDTO;
import com.easywecom.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 消息推送
 *
 * @author: 1*+
 * @date: 2021-08-18 17:08
 */
@Component
@SuppressWarnings("all")
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeMessagePushClient {


    /**
     * 发送应用消息
     */
    @Post(url = "/message/send")
    WeMessagePushResultDTO sendMessageToUser(@Body WeMessagePushDTO weMessagePushDto, @Header("agentId") String agentId, @Header("corpid") String corpId);

    /**
     * 应用推送消息
     */
    @Post(url = "/appchat/send")
    WeMessagePushResultDTO sendMessageToUserGroup(@Body WeMessagePushGroupDTO weMessagePushGroupDto, @Header("corpid") String corpId);

}
