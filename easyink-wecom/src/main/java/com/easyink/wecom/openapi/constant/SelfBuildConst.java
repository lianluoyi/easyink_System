package com.easyink.wecom.openapi.constant;

/**
 * 自建应用常量
 * @author tigger
 * 2024/11/20 17:10
 **/
public class SelfBuildConst {

    /**
     * 请求待开发自建应用校验地址是否可用url
     */
    public final static String CHECK_URL = "/unionId/checkUrl";
    /**
     * 请求待开发自建应用解密externalUserId接口的url
     */
    public final static String DECRYPT_EXTERNAL_USERID_REQ_URL = "/unionId/getFromServiceExternalUserId";
    /**
     * 解密员工id uri
     */
    public final static String DECRYPT_USERID_REQ_URL = "/unionId/getOpenUserIdToUserId";
}
