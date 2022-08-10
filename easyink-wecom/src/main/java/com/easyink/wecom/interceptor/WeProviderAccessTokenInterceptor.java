package com.easyink.wecom.interceptor;

import cn.hutool.json.JSONUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import com.easyink.common.constant.WeConstans;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.service.WeAccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 类名: 服务商AccessToken拦截器
 *
 * @author: 1*+
 * @date: 2021-12-24 15:14
 */
@Slf4j
@Component
public class WeProviderAccessTokenInterceptor implements Interceptor<Object> {


    private final WeAccessTokenService weAccessTokenService;

    @Lazy
    public WeProviderAccessTokenInterceptor(WeAccessTokenService weAccessTokenService) {
        this.weAccessTokenService = weAccessTokenService;
    }


    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>uri：{}", request.getUrl());
        //服务商token
        String token = weAccessTokenService.findProviderAccessToken();
        request.addQuery("access_token", token);
        request.setDataType(ForestDataType.JSON);
        request.setContentType("application/json");
        return true;
    }


    /**
     * 请求发送失败时被调用
     *
     * @param e              e
     * @param forestRequest  forestRequest
     * @param forestResponse forestResponse
     */
    @Override
    public void onError(ForestRuntimeException e, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.error("请求失败url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
    }


    /**
     * 请求成功调用(微信端错误异常统一处理)
     *
     * @param o              o
     * @param forestRequest  forestRequest
     * @param forestResponse forestResponse
     */
    @Override
    public void onSuccess(Object o, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.info("url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
        WeResultDTO weResultDto = JSONUtil.toBean(forestResponse.getContent(), WeResultDTO.class);
        // 部分uri 错误码需要单独业务处理不抛出异常
        if (null != weResultDto.getErrcode() && !WeConstans.WE_SUCCESS_CODE.equals(weResultDto.getErrcode())
                && !WeConstans.NOT_EXIST_CONTACT.equals(weResultDto.getErrcode())) {
            throw new ForestRuntimeException(forestResponse.getContent());
        }

    }


}
