package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.customer.CustomerGroupDetail;
import com.easyink.wecom.domain.dto.customer.CustomerGroupList;
import com.easyink.wecom.domain.dto.group.GroupChatListReq;
import com.easyink.wecom.domain.dto.group.GroupChatListResp;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 客户群
 *
 * @author: 1*+
 * @date: 2021-08-18 17:05
 */
@Component
@EnableRetry(retryExceptionClass = RetryException.class)
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeCustomerGroupClient {




    /**
     * 获取客户群列表
     *
     * @param params
     * @return
     */
    @Post(url = "/externalcontact/groupchat/list")
    CustomerGroupList groupChatLists(@Body CustomerGroupList.Params params, @Header("corpid") String corpId);


    /**
     * 获取客户群详情
     *
     * @param params
     * @return
     */
    @Post(url = "/externalcontact/groupchat/get")
    CustomerGroupDetail groupChatDetail(@Body CustomerGroupDetail.Params params, @Header("corpid") String corpId);

    /**
     * 获取客户群列表
     *
     * @param req    {@link GroupChatListReq}
     * @param corpId 企业id
     * @return { @link GroupChatListResp}
     */
    @Post(url = "/externalcontact/groupchat/list")
    GroupChatListResp groupChatList(@Body GroupChatListReq req, @Header("corpId") String corpId);
}