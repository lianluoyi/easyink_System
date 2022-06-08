package com.easywecom.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类名: 会话存档RSA秘钥配置
 *
 * @author : silver_chariot
 * @date : 2022/5/30 20:50
 */
@Component
@ConfigurationProperties(prefix = "chatrsakey")
@Data
public class ChatRsaKeyConfig {

    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 秘钥
     */
    private String privateKey;
}
