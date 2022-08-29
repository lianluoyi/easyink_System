package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.wecom.domain.dto.CorpIdToOpenCorpIdResp;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import com.easyink.wecom.interceptor.WeProviderAccessTokenInterceptor1;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName： weUpdateIDClient
 * 企业微信帐号ID安全性全面升级 替换corpId，userId，external_userId
 * https://developer.work.weixin.qq.com/document/path/95327
 *
 * @author wx
 * @date 2022/8/22 17:56
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}")
public interface WeUpdateIDClient {

    /**
     * 转换corpId，将明文corpid转换为第三方应用获取的corpid
     * 用provider_access_token和corpId获取
     * @param queryCorpId   待获取的企业ID
     * @return
     */
    @Post(url = "/service/corpid_to_opencorpid", interceptor = WeProviderAccessTokenInterceptor1.class)
    CorpIdToOpenCorpIdResp getOpenCorpId(@Body("corpid") String queryCorpId);

    /**
     * 将企业主体下的明文userid转换为服务商主体下的密文userid。
     *
     * @param corpId        用来获取accessToken
     * @param userIdList    需要转换的userIdList
     * @return
     */
    @Post(url = "batch/userid_to_openuserid", interceptor = WeAccessTokenInterceptor.class)
    CorpIdToOpenCorpIdResp getNewUserId(@Header("corpid") String corpId, @Body("userid_list") List<String>  userIdList);

    /**
     * 将企业主体下的external_userid转换为服务商主体下的external_userid。
     *
     * @param corpId            用来获取accessToken
     * @param externUserIdList  需要转换的external_userIdList
     * @return
     */
    @Post(url = "/externalcontact/get_new_external_userid", interceptor = WeAccessTokenInterceptor.class)
    CorpIdToOpenCorpIdResp getNewExternalUserid(@Header("corpid") String corpId, @Body("external_userid_list") List<String> externUserIdList);

    /**
     * 服务商完成了代开发应用的新旧id（userid/corpid/external_userid)的迁移，即可主动将该企业设置为“迁移完成”，设置之后，该代开发应用获取到的将是升级后的id
     * https://developer.work.weixin.qq.com/document/path/95865#51-%E4%BB%A3%E5%BC%80%E5%8F%91%E5%BA%94%E7%94%A8%E8%AE%BE%E7%BD%AE%E8%BF%81%E7%A7%BB%E5%AE%8C%E6%88%90
     * 用provider_access_token和以下参数进行调用
     * @param queryCorpId   企业corpid
     * @param agentId       企业代开发应用id
     * @param openIdType    id类型：1-userid与corpid; 3-external_userid
     * @return
     */
    @Post(url = "/service/finish_openid_migration", interceptor = WeProviderAccessTokenInterceptor1.class)
    WeResultDTO finishOpenIdMigration(@Body("corpid") String queryCorpId, @Body("agentid") String agentId, @Body("openid_type") Integer[] openIdType);
}
