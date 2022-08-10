package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.wecom.domain.dto.WeAccessUserInfo3rdDTO;
import com.easyink.wecom.domain.dto.WeUserDTO;
import com.easyink.wecom.domain.dto.app.ToOpenCorpIdResp;
import com.easyink.wecom.domain.dto.app.UserIdToOpenUserIdResp;
import com.easyink.wecom.interceptor.We3rdAccessTokenInterceptor;
import com.easyink.wecom.interceptor.WeProviderAccessTokenInterceptor1;
import com.easyink.wecom.interceptor.WeSuiteAccessTokenWebLoginInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类名: We3rdUserClient
 *
 * @author: 1*+
 * @date: 2021-09-13 9:19
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}")
public interface We3rdUserClient {


    /**
     * 根据用户账号,获取用户详情信息
     *
     * @param corpid 企业ID
     * @param userid 用户ID
     * @return
     */
    @Get(url = "/user/get", interceptor = We3rdAccessTokenInterceptor.class)
    WeUserDTO getUserByUserId(@Header("corpid") String corpid, @Query("userid") String userid);


    /**
     * useridList转换
     *
     * @param userIdList 获取到的成员ID
     * @param corpId     企业ID
     * @return {@link UserIdToOpenUserIdResp}
     */
    @Post(url = "/batch/userid_to_openuserid", interceptor = We3rdAccessTokenInterceptor.class)
    UserIdToOpenUserIdResp useridToOpenuserid(@Body("userid_list") List<String> userIdList, @Header("corpid") String corpId);


    /**
     * corpid转换
     *
     * @param corpId @Body("corpid")企业ID
     * @return {@link ToOpenCorpIdResp}
     */
    @Post(url = "/service/corpid_to_opencorpid", interceptor = WeProviderAccessTokenInterceptor1.class)
    ToOpenCorpIdResp toOpenCorpid(@Body("corpid") String corpId);


    /**
     * 获取访问用户身份
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     * @return {@link WeAccessUserInfo3rdDTO}
     */
    @Get(url = "/service/getuserinfo3rd",interceptor = WeSuiteAccessTokenWebLoginInterceptor.class)
    WeAccessUserInfo3rdDTO getuserinfo3rd(@Query("code") String code);
}
