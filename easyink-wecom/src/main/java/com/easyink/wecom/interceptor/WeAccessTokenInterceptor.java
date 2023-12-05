package com.easyink.wecom.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import com.easyink.common.config.WeComeConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.exception.RetryException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.service.WeAccessTokenService;
import com.easyink.wecom.service.WeCorpAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.util.Arrays;

/**
 * 类名: 自建应用AccessToken拦截器
 *
 * @author: 1*+
 * @date: 2021-12-27 10:46
 */
@Slf4j
@Component
public class WeAccessTokenInterceptor implements Interceptor<Object> {


    private final WeAccessTokenService weAccessTokenService;
    private final WeComeConfig weComeConfig;
    private final ForestConfiguration forestConfiguration;
    private final String urlPrefix;
    private final WeCorpAccountService weCorpAccountService;

    @Lazy
    public WeAccessTokenInterceptor() {
        weComeConfig = SpringUtils.getBean(WeComeConfig.class);
        weAccessTokenService = SpringUtils.getBean(WeAccessTokenService.class);
        forestConfiguration = SpringUtils.getBean(ForestConfiguration.class);
        weCorpAccountService = SpringUtils.getBean(WeCorpAccountService.class);
        String weComServerUrl = String.valueOf(forestConfiguration.getVariableValue(WeConstans.WECOM_SERVER_URL));
        String weComePrefix = String.valueOf(forestConfiguration.getVariableValue(WeConstans.WECOM_PREFIX));
        this.urlPrefix = weComServerUrl + weComePrefix;
    }


    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        String uri = request.getUrl().replace(urlPrefix, "");
        StringBuilder body = encodeToStringBody(request, uri);
        if (!Arrays.asList(weComeConfig.getFileUplodUrl()).contains(uri)) {
            request.setDataType(ForestDataType.JSON);
            request.setContentType("application/json");
        }
        // 添加请求参数access_token+
        if (PatternMatchUtils.simpleMatch(weComeConfig.getNoAccessTokenUrl(), uri)) {
            return true;
        }
        String token;
        String tokenKeyName = "access_token";
        String corpid = request.getHeaderValue("corpid");

        if (PatternMatchUtils.simpleMatch(weComeConfig.getNeedContactTokenUrl(), uri)) {
            //需要联系人token
            token = weAccessTokenService.findContactAccessToken(corpid);
        } else if (PatternMatchUtils.simpleMatch(weComeConfig.getNeedChatTokenUrl(), uri)) {
            //需要会话存档token
            token = weAccessTokenService.findChatAccessToken(corpid);
        } else if (PatternMatchUtils.simpleMatch(weComeConfig.getThirdAppUrl(), uri)) {
            //内部应用token
            String agentId = StrUtil.isEmpty(request.getHeaderValue(WeConstans.THIRD_APP_PARAM_TIP)) ?
                    (String) request.getQuery(WeConstans.THIRD_APP_PARAM_TIP) : request.getHeaderValue(WeConstans.THIRD_APP_PARAM_TIP);
            // V1.36.0，企微调整，去除了客户联系token，转为使用内部应用token，如果从请求头或Query参数中未获取到应用id，则根据corpId获取
            if (StringUtils.isBlank(agentId)) {
                agentId = weCorpAccountService.getAgentId(corpid);
            }
            token = weAccessTokenService.findInternalAppAccessToken(agentId, corpid);
        } else {
            token = weAccessTokenService.findCommonAccessToken(corpid);
        }
        request.addQuery(tokenKeyName, token);
        if (StringUtils.isBlank(body)) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>uri：{}, query: {}", uri, request.getQueryString());
        } else {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>uri：{}, query: {}, body: {}", uri, request.getQueryString(), body);
        }
        return true;
    }


    /**
     * 请求发送失败时被调用
     *
     * @param e
     * @param forestRequest
     * @param forestResponse
     */
    @Override
    public void onError(ForestRuntimeException e, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.error("请求失败url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
    }

//    @Override
//    public void onRetry(ForestRequest request, ForestResponse response) {
//        log.error("准备重试, url:【{}】,result:【{}】,retryCnt:【{}】", request.getUrl(), response.getContent() ,request.getCurrentRetryCount());
//    }

    /**
     * 请求成功调用(微信端错误异常统一处理)
     *
     * @param o
     * @param forestRequest
     * @param forestResponse
     */
    @Override
    public void onSuccess(Object o, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.info("url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());  
        WeResultDTO weResultDto = JSONUtil.toBean(forestResponse.getContent(), WeResultDTO.class);
        // 匹配需要判断的code，抛出指定异常
        if (needRetry(weResultDto.getErrcode())) {
            throw new RetryException(forestResponse.getContent());
        }
        // 部分uri 错误码需要单独业务处理不抛出异常
        String uri = forestRequest.getUrl().replace(urlPrefix, "");
        if (PatternMatchUtils.simpleMatch(weComeConfig.getNeedErrcodeUrl(), uri)) {
            return;
        }
        // 其他情况抛出异常
        if (null != weResultDto.getErrcode() && !WeConstans.WE_SUCCESS_CODE.equals(weResultDto.getErrcode()) && !WeConstans.NOT_EXIST_CONTACT.equals(weResultDto.getErrcode())) {
            throw new ForestRuntimeException(forestResponse.getContent());
        }
    }

    /**
     * 是否需要抛出特定异常重试
     *
     * @param code 响应的code
     * @return true 是，false 否
     */
    private boolean needRetry(Integer code) {
        if (code == null) {
            return false;
        }
        Integer[] codes = weComeConfig.getNeedRetryCode();
        for (Integer integer : codes) {
            if (integer.equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取打印的Body内容
     *
     * @param request {@link ForestRequest}
     * @param uri 请求uri
     * @return
     */
    private StringBuilder encodeToStringBody(ForestRequest request, String uri) {
        StringBuilder body = null;
        try {
            body = new StringBuilder();
            Object[] objects = request.getBody().stream().toArray();
            for (Object object : objects) {
                body.append(object.toString());
            }
        } catch (Exception e) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>打印body参数异常，uri:{}, query:{}, ex:{}", uri, request.getQueryString(), ExceptionUtils.getStackTrace(e));
        }
        return body;
    }
}
