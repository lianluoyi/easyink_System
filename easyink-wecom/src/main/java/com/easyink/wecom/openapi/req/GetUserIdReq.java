package com.easyink.wecom.openapi.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取解密externalUserId Req
 * @author tigger
 * 2024/11/19 16:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserIdReq {
    /**
     * 服务商openuserid
     */
    private List<String> openUserIdList;
    /**
     * 应用id
     */
    private Integer agentId;
}
