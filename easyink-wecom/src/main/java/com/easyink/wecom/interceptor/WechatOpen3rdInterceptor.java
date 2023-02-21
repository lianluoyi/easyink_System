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
 * 微信开放平台-第三方平台接口访问拦截器
 *
 * @author wx
 * 2023/1/10 11:33
 **/
@Component
@Slf4j
public class WechatOpen3rdInterceptor implements Interceptor<Object> {
    private final WechatOpenService wechatOpenService;
    private final WechatOpenConfig wechatOpenConfig;
    private static final String API_PREFIX = "https://api.weixin.qq.com/cgi-bin";

    private static final String COMPONENT_APPID = "component_appid";
    private static final String COMPONENT_ACCESS_TOKEN = "component_access_token";

    @Lazy
    public WechatOpen3rdInterceptor(WechatOpenService wechatOpenService, WechatOpenConfig wechatOpenConfig) {
        this.wechatOpenService = wechatOpenService;
        this.wechatOpenConfig = wechatOpenConfig;
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String uri = request.getUrl().replace(API_PREFIX, "");
        WechatOpenConfig.Platform3rdAccount platform3rdAccount = wechatOpenConfig.getPlatform3rdAccount();
        request.addBody(COMPONENT_APPID, platform3rdAccount.getAppId());
        if (PatternMatchUtils.simpleMatch(platform3rdAccount.getNeedTokenUrl(), uri)) {
            // 为请求设置access_token
            String componentAccessToken = wechatOpenService.getPlatform3rdAccessToken(platform3rdAccount.getAppId());
            request.addQuery(COMPONENT_ACCESS_TOKEN, componentAccessToken);
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
