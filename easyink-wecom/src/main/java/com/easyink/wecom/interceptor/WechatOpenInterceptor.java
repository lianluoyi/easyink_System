package com.easyink.wecom.interceptor;


import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.easyink.common.config.WechatOpenConfig;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

/**
 * 类名: 微信公众平台接口访问拦截器
 *
 * @author : silver_chariot
 * @date : 2022/7/20 11:12
 **/
@Component
@Slf4j
public class WechatOpenInterceptor implements Interceptor<Object> {


    private final WechatOpenService wechatOpenService;
    private final WechatOpenConfig wechatOpenConfig;
    private final static String API_PREFIX = "https://api.weixin.qq.com";

    @Lazy
    public WechatOpenInterceptor(WechatOpenService wechatOpenService, WechatOpenConfig wechatOpenConfig) {
        this.wechatOpenService = wechatOpenService;
        this.wechatOpenConfig = wechatOpenConfig;
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String uri = request.getUrl().replace(API_PREFIX, "");
        if (PatternMatchUtils.simpleMatch(wechatOpenConfig.getOfficialAccount().getNeedTokenUrl(), uri)) {
            // 为请求设置access_token
            wechatOpenService.setAccessToken(request);
        }
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("url:【{}】,result:【{}】", request.getUrl(), response.getContent());
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.error("请求失败url:【{}】,result:【{}】", request.getUrl(), response.getContent());
    }
}
