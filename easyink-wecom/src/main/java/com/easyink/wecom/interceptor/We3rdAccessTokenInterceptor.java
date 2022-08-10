package com.easyink.wecom.interceptor;

import cn.hutool.json.JSONUtil;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestDataType;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.service.WeAccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 类名: 三方授权企业AccessToken拦截器
 *
 * @author: 1*+
 * @date: 2021-09-14 10:17
 */
@Slf4j
@Component
public class We3rdAccessTokenInterceptor implements Interceptor<Object> {


    private final WeAccessTokenService weAccessTokenService;

    @Lazy
    public We3rdAccessTokenInterceptor(WeAccessTokenService weAccessTokenService) {
        this.weAccessTokenService = weAccessTokenService;
    }


    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>uri：{}", request.getUrl());

        request.setDataType(ForestDataType.JSON);
        request.setContentType("application/json");
        // 添加请求参数access_token

        String token;
        String tokenKeyName = "access_token";

        String corpId = request.getHeaderValue("corpid");
        if (StringUtils.isBlank(corpId)) {
            throw new WeComException("缺少请求头corpid参数");
        }
        token = weAccessTokenService.find3rdAppCorpAuthAccessToken(corpId);
        request.addQuery(tokenKeyName, token);
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
        if (null != weResultDto.getErrcode() && !WeConstans.WE_SUCCESS_CODE.equals(weResultDto.getErrcode()) && !WeConstans.NOT_EXIST_CONTACT.equals(weResultDto.getErrcode())) {
            throw new ForestRuntimeException(forestResponse.getContent());
        }

    }


}
