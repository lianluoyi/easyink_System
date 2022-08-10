package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easyink.wecom.domain.dto.message.QueryCustomerMessageStatusResultDTO;
import com.easyink.wecom.domain.dto.message.QueryCustomerMessageStatusResultDataObjectDTO;
import com.easyink.wecom.domain.dto.message.SendMessageResultDTO;
import com.easyink.wecom.domain.dto.message.WeCustomerMessagePushDTO;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 群发消息
 *
 * @author: 1*+
 * @date: 2021-08-18 17:05
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeCustomerMessagePushClient {


    /**
     * 添加企业群发消息任务
     * <a href="https://work.weixin.qq.com/api/doc/90000/90135/92135">API文档地址</a>
     */
    @Post(url = "/externalcontact/add_msg_template", timeout = 10000)
    SendMessageResultDTO sendCustomerMessageToUser(@Body WeCustomerMessagePushDTO customerMessagePushDto, @Header("corpid") String corpId);

    /**
     * 获取企业群发消息发送结果
     * <a href="https://work.weixin.qq.com/api/doc/90000/90135/93338#%E8%8E%B7%E5%8F%96%E4%BC%81%E4%B8%9A%E7%BE%A4%E5%8F%91%E6%88%90%E5%91%98%E6%89%A7%E8%A1%8C%E7%BB%93%E6%9E%9C">API文档地址</a>
     *
     * @param queryCustomerMessageStatusResultDataObjectDTO{msgid} <a href="https://work.weixin.qq.com/api/doc/90000/90135/92135">添加企业群发消息任务返回的msgid</a>
     */
    @Post(url = "/externalcontact/get_group_msg_result")
    QueryCustomerMessageStatusResultDTO queryCustomerMessageStatus(@Body QueryCustomerMessageStatusResultDataObjectDTO queryCustomerMessageStatusResultDataObjectDTO, @Header("corpid") String corpId);


}
