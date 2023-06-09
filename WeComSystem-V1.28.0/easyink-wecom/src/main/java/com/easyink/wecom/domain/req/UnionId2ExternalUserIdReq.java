package com.easyink.wecom.domain.req;

import lombok.Data;

/**
 * 类名: unionid转客户id请求
 *
 * @author : silver_chariot
 * @date : 2022/7/21 16:37
 **/
@Data
public class UnionId2ExternalUserIdReq {
    /**
     * 微信客户unionid
     */
    private String unionid;
    /**
     * 微信客户openid
     */
    private String openid;
}
