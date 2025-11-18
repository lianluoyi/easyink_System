package com.easyink.common.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES256加密策略实现
 */
@Getter
@Setter
@Component
public class AES256EncryptionStrategy implements EncryptionStrategy {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private  String secretKey;


    @Override
    public String encrypt(String original) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            
            byte[] encryptedBytes = cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
            return "ENC(" + Base64.getEncoder().encodeToString(encryptedBytes) + ")";
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String decrypt(String encrypted) {
        try {
            // 去除"ENC("和")"前缀和后缀
            encrypted = encrypted.substring(4, encrypted.length() - 1);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败: " + e.getMessage(), e);
        }
    }
}