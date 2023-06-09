package com.easyink.wecom.domain.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信开放平台-第三方平台返回component_access_token
 *
 * @author wx
 * 2023/1/11 10:05
 **/
@NoArgsConstructor
@Data
public class ComponentAccessTokenResp {
    /**
     * 第三方平台access_token
     */
    private String component_access_token;

    /**
     * 过期时间
     */
    private Integer expires_in;
}
