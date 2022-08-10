package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easywecom.wecom.domain.dto.*;
import com.easywecom.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 类名: 企业微信通讯录成员
 *
 * @author: 1*+
 * @date: 2021-08-18 17:16
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeUserClient {



    /**
     * 创建用户
     *
     * @param weUserDto
     * @return
     */
    @Post(url = "/user/create")
    WeResultDTO createUser(@Body WeUserDTO weUserDto, @Header("corpid") String corpId);


    /**
     * 根据用户账号,获取用户详情信息
     *
     * @param userid
     * @return
     */
    @Get(url = "/user/get")
    WeUserDTO getUserByUserId(@Query("userid") String userid, @Header("corpid") String corpId);


    /**
     * 更新通讯录用户
     *
     * @param weUserDto
     * @return
     */
    @Post(url = "/user/update")
    WeResultDTO updateUser(@Body WeUserDTO weUserDto, @Header("corpid") String corpId);


    /**
     * 根据账号删除指定用户
     *
     * @param userid
     * @return
     */
    @Get(url = "/user/delete")
    WeResultDTO deleteUserByUserId(@Query("userid") String userid, @Header("corpid") String corpId);


    /**
     * 获取部门成员
     *
     * @param departmentId
     * @param fetchChild
     * @return
     */
    @Get(url = "/user/simplelist")
    WeUserListDTO simpleList(@Query("department_id") Long departmentId, @Query("fetch_child") Integer fetchChild, @Header("corpid") String corpId);

    /**
     * 获取部门成员
     *
     * @param departmentId
     * @param fetchChild
     * @return
     */
    @Get(url = "/user/list")
    WeUserListDTO list(@Query("department_id") Long departmentId, @Query("fetch_child") Integer fetchChild, @Header("corpid") String corpId);

    /**
     * 分配客户
     *
     * @return
     */
    @Post(url = "/externalcontact/transfer")
    WeResultDTO allocateCustomer(@Body AllocateWeCustomerDTO allocateWeCustomerDTO, @Header("corpid") String corpId);

    /**
     * 分配客户
     *
     * @return
     */
    @Post(url = "/externalcontact/resigned/transfer_customer")
    WeCustomerExtendRest newAllocateCustomer(@Body AllocateWeCustomerV2DTO allocateWeCustomerV2DTO, @Header("corpid") String corpId);


    /**
     * 分配成员群
     *
     * @return
     */
    @Post(url = "/externalcontact/groupchat/transfer")
    WeGroupExtendDTO allocateGroup(@Body AllocateWeGroupDTO allocateWeGroupDTO, @Header("corpid") String corpId);


    /**
     * 获取离职员工列表
     *
     * @return
     */
    @Post(url = "/externalcontact/get_unassigned_list")
    LeaveWeUserListsDTO leaveWeUsers(@Body Map<String, Object> query, @Header("corpid") String corpId);


    /**
     * 获取访问用户身份(内部应用)
     *
     * @param code
     * @param agentId 应用的id,请求头中
     * @return
     */
    @Get(url = "/user/getuserinfo")
    WeUserInfoDTO getuserinfo(@Query("code") String code, @Header("agentId") String agentId, @Header("corpid") String corpId);

    /**
     * 获取扫码登录的企微用户信息
     *
     * @param code 扫码回调的CODE
     * @return 用户信息
     */
    @Get(url = "/user/getuserinfo")
    WeUserInfoDTO getQrCodeLoginUserInfo(@Query("code") String code, @Header("corpid") String corpId);

    /**
     * 获取加入企业二维码
     *
     * @param corpId 企业ID
     * @return {@link GetJoinQrCodeResp}
     */
    @Get(url = "/corp/get_join_qrcode")
    GetJoinQrCodeResp getJoinQrCode(@Header("corpId") String corpId);
}
