package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.easyink.wecom.interceptor.ThirdPushInterceptor;
import org.springframework.stereotype.Component;

/**
 * 第三方推送Forest客户端
 *
 * @author easyink
 * @date 2024-01-01
 */
@Component
@BaseRequest(
    contentType = "application/json",
        interceptor = ThirdPushInterceptor.class
)
public interface WeThirdPartyPushClient {

//    /**
//     * 推送数据到第三方服务（带授权）- 使用URL占位符
//     *
//     * @param url 推送URL（完整URL）
//     * @param data 推送数据
//     * @return 响应结果
//     */
//    @Post(url = "{url}")
//    ForestResponse<String> pushData(@Var(value = "url") String url,
//                                   @Body String data);


}
