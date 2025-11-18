package com.easyink.wecom.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * 地图API密钥拦截器
 * 只负责将请求中的key参数替换为实际的API密钥
 * API调用限制逻辑已移至MapServiceImpl
 *
 * @author wx
 * @date 2023/8/8
 */
@Slf4j
public class ThirdPushInterceptor implements Interceptor<Object> {

    /**
     * 请求成功后的处理
     */
    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("[表单推送] url:【{}】,result:【{}】", request.getUrl(), response.getContent());
    }

    /**
     * 请求失败后的处理
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.error("[表单推送]请求失败url:【{}】,result:【{}】", request.getUrl(), response.getContent());
    }

} 