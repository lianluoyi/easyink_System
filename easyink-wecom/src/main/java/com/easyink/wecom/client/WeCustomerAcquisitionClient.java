package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.emplecode.CustomerAssistantDTO;
import com.easyink.wecom.domain.dto.emplecode.CustomerAssistantResp;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 获客助手相关接口
 *
 * @author lichaoyu
 * @date 2023/8/23 10:41
 */
@Component
@EnableRetry(retryExceptionClass = RetryException.class)
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeCustomerAcquisitionClient {

    /**
     * 创建获客链接
     *
     * @param customerAssistantDTO {@link CustomerAssistantDTO}
     * @param corpId 企业ID
     * @return {@link CustomerAssistantResp}
     */
    @Post(url = "/externalcontact/customer_acquisition/create_link")
    CustomerAssistantResp createLink(@Body CustomerAssistantDTO customerAssistantDTO, @Header("corpid") String corpId);

    /**
     * 更新获客链接
     *
     * @param customerAssistantDTO {@link CustomerAssistantDTO}
     * @param corpId 企业ID
     * @return {@link CustomerAssistantResp}
     */
    @Post(url = "/externalcontact/customer_acquisition/update_link")
    CustomerAssistantResp updateLink(@Body CustomerAssistantDTO customerAssistantDTO, @Header("corpid") String corpId);

    /**
     * 删除获客链接
     *
     * @param customerAssistantDTO {@link CustomerAssistantDTO}
     * @param corpId 企业ID
     * @return {@link CustomerAssistantResp}
     */
    @Post(url = "/externalcontact/customer_acquisition/delete_link")
    CustomerAssistantResp delLink(@Body CustomerAssistantDTO customerAssistantDTO, @Header("corpid") String corpId);

    /**
     * 查询获客链接剩余使用量
     *
     * @param corpId 企业ID
     * @return {@link CustomerAssistantResp.Quota}
     */
    @Get(url = "/externalcontact/customer_acquisition_quota")
    CustomerAssistantResp.Quota quota(@Header("corpid") String corpId);
}
