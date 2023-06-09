package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.wecom.client.retry.OprFreqRetryWhen;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.domain.dto.customer.*;
import com.easyink.wecom.domain.dto.customer.req.GetByUserReq;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import com.easyink.wecom.domain.query.GroupChatStatisticQuery;
import com.easyink.wecom.domain.query.UserBehaviorDataQuery;
import com.easyink.wecom.domain.resp.UnionId2ExternalUserIdResp;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 客户联系
 *
 * @author: 1*+
 * @date: 2021-08-18 17:04
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeCustomerClient {


    /**
     * 获取配置了客户联系功能的成员列表
     *
     * @return
     */
    @Get(url = "/externalcontact/get_follow_user_list")
    FollowUserList getFollowUserList(@Header("corpid") String corpId);


    /**
     * 根据客户id获取客户详情
     *
     * @param externalUserid
     * @return
     */
    @Get(url = "/externalcontact/get")
    ExternalUserDetail get(@Query("external_userid") String externalUserid, @Header("corpid") String corpId);

    /**
     * 获取外部联系人详情
     *
     * @param externalUserid 外部联系人ID
     * @param corpId         企业ID
     * @return
     */
    @Get(url = "/externalcontact/get")
    GetExternalDetailResp getV2(@Query("external_userid") String externalUserid, @Header("corpid") String corpId);

    /**
     * 企业/第三方可通过此接口获取指定成员添加的客户信息列表。
     *
     * @param req    {@link GetByUserReq}
     * @param corpId 企业id
     * @return {@link GetByUserResp}
     */
    @Post(url = "/externalcontact/batch/get_by_user")
    GetByUserResp getByUser(@Body GetByUserReq req, @Header("corpId") String corpId);


    /**
     * 修改客户备注信息
     *
     * @param weCustomerRemark
     * @return
     */
    @Post(url = "/externalcontact/remark")
    WeResultDTO remark(@Body WeCustomerDTO.WeCustomerRemark weCustomerRemark, @Header("corpid") String corpId);


    /**
     * 编辑客户标签
     *
     * @return
     */
    @Post(url = "/externalcontact/mark_tag")
    @Retry(maxRetryInterval = "1000" ,maxRetryCount =  "3" , condition = OprFreqRetryWhen.class)
    WeResultDTO makeCustomerLabel(@Body CustomerTagEdit customerTagEdit, @Header("corpid") String corpId);


    /**
     * 客户发送欢迎语
     */
    @Post(url = "/externalcontact/send_welcome_msg")
    WeResultDTO sendWelcomeMsg(@Body WeWelcomeMsg wxCpWelcomeMsg, @Header("corpid") String corpId);


    /**
     * unionid转换external_userid
     *
     * @return
     */
    @Post(url = "/externalcontact/unionid_to_external_userid")
    ExternalUserDetail unionidToExternalUserid(@Body ExternalContact unionid, @Header("corpid") String corpId);


    /**
     * 联系客户统计
     *
     * @return
     */
    @Post(url = "/externalcontact/get_user_behavior_data")
    UserBehaviorDataDTO getUserBehaviorData(@JSONBody UserBehaviorDataQuery query, @Header("corpid") String corpId);

    /**
     * 群聊数据统计（按群主聚合的方式）
     *
     * @return
     */
    @Post(url = "/externalcontact/groupchat/statistic")
    GroupChatStatisticDTO getGroupChatStatistic(@JSONBody GroupChatStatisticQuery query, @Header("corpid") String corpId);

    /**
     * 群聊数据统计(按自然日聚合的方式)
     *
     * @return
     */
    @Post(url = "/externalcontact/groupchat/statistic_group_by_day")
    GroupChatStatisticDTO getGroupChatStatisticGroupByDay(@JSONBody GroupChatStatisticQuery query, @Header("corpid") String corpId);


    @Post("/externalcontact/unionid_to_external_userid")
    UnionId2ExternalUserIdResp unionId2ExternalUserId (@Body("unionid")String unionid ,@Body("openid")String openid) ;


    /**
     * unionid转换为第三方external_userid
     * 当微信用户进入服务商的小程序或公众号时，服务商可通过此接口，
     * 将微信客户的unionid转为第三方主体的external_userid，若该微信用户尚未成为企业的客户，则返回pending_id
     * https://developer.work.weixin.qq.com/document/path/95926
     *
     * @param unionId   	微信客户的unionid
     * @param openId        微信客户的openid
     * @param subjectType   {@link com.easyink.common.enums.customer.SubjectTypeEnum} （当前使用该接口默认传1）
     * @param corpId        企业id 用来获取accessToken
     *
     * @return ExternalUserDetail#external_userid
     */
    @Post(url = "/idconvert/unionid_to_external_userid")
    ExternalUserDetail getExternalUserIdByUnionIdAndOpenId(@Body("unionid") String unionId,
                                                           @Body("openid") String openId,
                                                           @Body("subject_type") Integer subjectType,
                                                           @Header("corpid") String corpId);
}
