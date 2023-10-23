package com.easyink.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @Description: 企业微信配置文件相关
 * @Date: create in 2020/9/3 0003 23:25
 */
@Component
@ConfigurationProperties(prefix = "wecome")
@Data
public class WeComeConfig {
    /**
     * 企业微信后台地址
     */
    private String serverUrl;

    /**
     * 企业微信后台地址前缀
     */
    private String weComePrefix;

    /**
     * 企业微信端无需token的url
     */
    private String[] noAccessTokenUrl;

    /**
     * 需要使用联系人token的url
     */
    private String[] needContactTokenUrl;

    /**
     * 需要使用客户联系token的url
     */
    private String[] needCustomTokenUrl;

    /**
     * 文件上传url
     */
    private String[] fileUplodUrl;


    /**
     * 会话存档所需token 的url
     */
    private String[] needChatTokenUrl;

    /**
     * 第三方自建应用得url
     */
    private String[] thirdAppUrl;
    /**
     * 需要处理错误码的url
     */
    private String[] needErrcodeUrl;

    /**
     * 需要重试判断的Code
     */
    private Integer[] needRetryCode;

}
