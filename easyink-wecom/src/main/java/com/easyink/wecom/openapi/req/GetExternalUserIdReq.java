package com.easyink.wecom.openapi.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取解密externalUserId Req
 * @author tigger
 * 2024/11/19 16:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetExternalUserIdReq {
    /**
     * 企微外部联系人userid
     */
    private String externalUserId;
    /**
     * 应用id
     */
    private Integer agentId;
}
