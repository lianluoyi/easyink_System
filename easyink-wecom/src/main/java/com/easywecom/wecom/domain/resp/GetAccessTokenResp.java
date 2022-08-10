package com.easywecom.wecom.domain.resp;

import lombok.Data;

/**
 * 类名: 获取小程序accessToken响应实体
 *
 * @author : silver_chariot
 * @date : 2022/7/20 14:58
 **/
@Data
public class GetAccessTokenResp extends WechatOpenBaseResp {
    /**
     * 获取到的凭证
     */
    private String access_token;
    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private Integer expires_in;

}
