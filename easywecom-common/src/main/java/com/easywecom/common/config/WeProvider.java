package com.easywecom.common.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 类名: WeProvider
 *
 * @author: 1*+
 * @date: 2021-12-24 11:12
 */
@Data
public class WeProvider extends WeCrypt implements Serializable {

    private String corpId;
    private String secret;
    private WebSuite webSuite;
    private DkSuite dkSuite;

    @Data
    public static class WebSuite extends WeCrypt implements Serializable {
        private String suiteId;
        private String secret;
    }

    @Data
    public static class DkSuite extends WeCrypt implements Serializable {
        private String dkId;
        private String secret;
        private String dkQrCode;
    }

    /**
     * 获取解密参数
     *
     * @param id corpId或者suiteId或者dkId
     * @return {@link WeCrypt}
     */
    public WeCrypt getCryptById(String id) {
        if (StringUtils.isBlank(id)) {
            return this;
        }

        if (id.equals(corpId)) {
            return this;
        } else if (webSuite != null && id.equals(webSuite.getSuiteId())) {
            return webSuite;
        } else if (dkSuite != null && id.equals(dkSuite.getDkId())) {
            return dkSuite;
        } else {
            return this;
        }
    }

    /**
     * 获取密钥
     *
     * @param id corpId或者suiteId或者dkId
     * @return {@link String}
     */
    public String getSecretById(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }

        if (id.equals(corpId)) {
            return this.getSecret();
        } else if (webSuite != null && id.equals(webSuite.getSuiteId())) {
            return webSuite.getSecret();
        } else if (dkSuite != null && id.equals(dkSuite.getDkId())) {
            return dkSuite.getSecret();
        } else {
            return "";
        }
    }
}
