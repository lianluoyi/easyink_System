package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easywecom.wecom.domain.dto.WeExternalContactDTO;
import com.easywecom.wecom.domain.dto.transfer.*;
import com.easywecom.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 员工对外联系方式
 *
 * @author: 1*+
 * @date: 2021-08-18 17:08
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeExternalContactClient {


    @Post(url = "/externalcontact/add_contact_way")
    WeExternalContactDTO addContactWay(@Body WeExternalContactDTO.WeContactWay weContactWay, @Header("corpid") String corpId);

    @Post(url = "/externalcontact/update_contact_way")
    WeExternalContactDTO updateContactWay(@Body WeExternalContactDTO.WeContactWay weContactWay, @Header("corpid") String corpId);

    @Post(url = "/externalcontact/del_contact_way")
    WeExternalContactDTO delContactWay(@Body WeExternalContactDTO.WeContactWay weContactWay, @Header("corpid") String corpId);

    @Post(url = "/externalcontact/get_contact_way")
    WeExternalContactDTO getContactWay(@Query("config_id") String configId, @Header("corpid") String corpId);

    /**
     * 在职分配
     *
     * @param req    {@link TransferCustomerReq}
     * @param corpId 企业id
     * @return
     */
    @Post(url = "/externalcontact/transfer_customer")
    TransferCustomerResp transferCustomer(@Body TransferCustomerReq req, @Header("corpid") String corpId);

    /**
     * 查询客户接替状态(在职继承)
     *
     * @param req    {@link TransferResultReq}
     * @param corpId 企业id
     * @return {@link TransferResultResp}
     */
    @Post(url = "/externalcontact/transfer_result")
    TransferResultResp transferResult(@Body TransferResultReq req, @Header("corpid") String corpId);

    /**
     * 查询客户接替状态(离职继承)
     *
     * @param req    {@link TransferResultResignedReq}
     * @param corpId 企业id
     * @return {@link TransferResultResignedResp }
     */
    @Post(url = "/externalcontact/resigned/transfer_result")
    TransferResultResignedResp transferResignedResult(@Body TransferResultResignedReq req, @Header("corpId") String corpId);

    /**
     * 获取待分配的离职员工详情
     *
     * @param req    {@link GetUnassignedListReq}
     * @param corpId 企业id
     * @return {@link GetUnassignedListResp}
     */
    @Post(url = "/externalcontact/get_unassigned_list")
    GetUnassignedListResp getUnassignedList(@Body GetUnassignedListReq req, @Header("corpId") String corpId);

    /**
     * 分配离职员工的客户
     *
     * @param req    {@link TransferResignedCustomerReq}
     * @param corpId 企业ID
     * @return {@link TransferResignedCustomerResp }
     */
    @Post(url = "/externalcontact/resigned/transfer_customer")
    TransferResignedCustomerResp transferResignedCustomer(@Body TransferResignedCustomerReq req, @Header("corpId") String corpId);

    /**
     * 分配离职员工的客户群
     *
     * @param req    {@link TransferResignedCustomerReq}
     * @param corpId 企业ID
     * @return {@link  TransferResignedGroupChatReq}
     */
    @Post(url = "/externalcontact/groupchat/transfer")
    TransferResignedGroupChatResp transferResignedGroup(@Body TransferResignedGroupChatReq req, @Header("corpId") String corpId);


}
