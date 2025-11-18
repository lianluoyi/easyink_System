package com.easyink.common.utils.wecom;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 类名: 企业秘钥解密工具
 *
 * @author : silver_chariot
 * @date : 2023/1/9 14:55
 **/
@Component
@ConfigurationProperties(prefix = "decrypt")
public class CorpSecretDecryptUtil {

    @Setter
    @Getter
    private String unionIdKey;
    private static final String RSA = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPPadding";

    /**
     * 解密unionId
     *
     * @param encryptUnionId 加密的unionId
     * @return unionId
     */
    public String decryptUnionId(String encryptUnionId) {
        return privateKeyDecrypt(encryptUnionId, unionIdKey);
    }

    /**
     * 根据私钥解密
     *
     * @param ciphertext       密文
     * @param privateKeyBase64 私钥
     * @return 明文
     */
    public static String privateKeyDecrypt(String ciphertext, String privateKeyBase64) {
        try {
            // 密文base64解码字节数组
            byte[] bytes = Base64Utils.decode(ciphertext.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            PrivateKey privateKey = getPrivateKey(privateKeyBase64);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int keyBit = getKeySize(privateKey);
            int inputLen = bytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            int step = keyBit / 8;

            for (int i = 0; inputLen - offSet > 0; offSet = i * step) {
                byte[] cache;
                if (inputLen - offSet > step) {
                    cache = cipher.doFinal(bytes, offSet, step);
                } else {
                    cache = cipher.doFinal(bytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                ++i;
            }
            // 明文字节数组
            byte[] plaintextBytes = out.toByteArray();
            out.close();
            return new String(plaintextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     *
     * @param privateKeyBase64 私钥base64
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64Utils.decode(privateKeyBase64.getBytes(StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取私钥长度
     *
     * @param privateKey 私钥
     * @return key size
     */
    public static int getKeySize(PrivateKey privateKey) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        return rsaPrivateKey.getModulus()
                            .bitLength();
    }

}
