package com.easywecom.wecom.domain.resp;

import com.easywecom.common.utils.StringUtils;
import lombok.Data;

/**
 * 类名: 获取公众号用户信息响应
 *
 * @author : silver_chariot
 * @date : 2022/7/20 10:42
 **/
@Data
public class GetOfficialAuthInfoResp extends WechatOpenBaseResp {
    /**
     * 用户在公众号唯一标识
     */
    private String openId;
    /**
     * access_token接口调用凭证超时时间，单位（秒）
     */
    private Integer expires_in;
    /**
     * 对应openid的access_token
     */
    private String access_token;

}
