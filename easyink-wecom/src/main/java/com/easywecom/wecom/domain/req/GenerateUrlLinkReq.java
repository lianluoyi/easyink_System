package com.easywecom.wecom.domain.req;

import com.easywecom.common.config.WechatOpenConfig;
import lombok.Data;

/**
 * 类名: 生成小程序跳转链接请求
 *
 * @author : silver_chariot
 * @date : 2022/7/20 13:59
 **/
@Data
public class GenerateUrlLinkReq {
    /**
     * （是）接口调用凭证
     */
    private String access_token;
    /**
     * (否)通过 URL Link 进入的小程序页面路径，必须是已经发布的小程序存在的页面，
     * 不可携带 query 。path 为空时会跳转小程序主页
     */
    private String path;
    /**
     * (否)通过 URL Link 进入小程序时的query，最大1024个字符，
     * 只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~%
     */
    private String query;
    /**
     * (否) 要打开的小程序版本。正式版为 "release"，
     * 体验版为"trial"，开发版为"develop"，仅在微信外打开时生效。
     */
    private String env_version;
    /**
     * (是) 小程序 URL Link 失效类型，失效时间：0，
     * 失效间隔天数：1
     */
    private Integer expire_type;
    /**
     * 到期失效的 URL Link 的失效时间，为 Unix 时间戳。
     * 生成的到期失效 URL Link 在该时间前有效。
     * 最长有效期为30天。expire_type 为 0 必填
     */
    private Integer expire_time;
    /**
     * 到期失效的URL Link的失效间隔天数。生成的到期失效URL Link在该间隔时间到达前有效。
     * 最长间隔天数为30天。expire_type 为 1 必填
     */
    private Integer expire_interval;

    public GenerateUrlLinkReq token(String accessToken) {
        this.access_token = accessToken;
        return this;
    }
    public GenerateUrlLinkReq query(String query) {
        this.query = query;
        return this;
    }

    public GenerateUrlLinkReq path(String path) {
        this.path = path;
        return this;
    }

    public GenerateUrlLinkReq version(String version) {
        this.env_version = version;
        return this;
    }

    public GenerateUrlLinkReq buildReq() {
        // 失效间隔天数类型
        this.expire_type = 1;
        // 设置失效天数为30
        this.expire_interval = 30;
        return this;
    }


}
