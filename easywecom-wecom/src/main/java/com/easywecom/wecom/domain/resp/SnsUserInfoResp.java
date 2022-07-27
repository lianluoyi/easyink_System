package com.easywecom.wecom.domain.resp;

import lombok.Data;

/**
 * 类名: 页面用户授权响应 例：
 * {
 * "openid": "oqL2307pDxHtihTAPrQjxQ",
 * "nickname": "MX",
 * "sex": 0,
 * "language": "",
 * "city": "",
 * "province": "",
 * "country": "",
 * "headimgurl": "https://thirdTfd31LA/132",
 * "privilege": [],
 * "unionid": "o3bNmo0KmfI"
 * }
 *
 * @author : silver_chariot
 * @date : 2022/7/22 18:31
 **/

@Data
public class SnsUserInfoResp extends WechatOpenBaseResp {
    /**
     * openid
     */
    private String openid;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 语言
     */
    private String language;
    /**
     * 城市
     */
    private String city;
    /**
     * 省份
     */
    private String province;
    /**
     * 国家
     */
    private String country;
    /**
     * 头像url
     */
    private String headimgurl;
    /**
     * unionid
     */
    private String unionid;

}
