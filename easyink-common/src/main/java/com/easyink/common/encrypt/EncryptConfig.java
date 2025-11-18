package com.easyink.common.encrypt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.encrypt")
public class EncryptConfig {
    
    /**
     * 是否启用DAO层加密
     */
    private boolean enabled = true;
    
    /**
     * 加密算法类型
     */
    private String algorithm = "AES";
    
    /**
     * 加密密钥
     */
    private String key = "Xqil9lioQx7uldYcOBJ/Lg==";

    @Primary
    @Bean
    public AES256EncryptionStrategy aes256EncryptionStrategy(EncryptConfig encryptConfig){
        AES256EncryptionStrategy strategy = new AES256EncryptionStrategy();
        strategy.setSecretKey(Base64.getEncoder().encodeToString(encryptConfig.getKey().getBytes(StandardCharsets.UTF_8)));
        return strategy;
    }
    
}