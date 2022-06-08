package com.easywecom.common.utils.wecom;

import com.easywecom.common.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * 类名: RSA工具类
 *
 * @author: 1*+
 * @date: 2021-08-18 9:18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RsaUtil {

    private static final String BC = "BC";

    /**
     * RSA pkcs1 2048bit 解密工具,
     * 获取私钥PrivateKey
     *
     * @param privateKeyPem 2048bit pkcs1格式,base64编码后的RSA字符串
     * @return PrivateKey, 用于解密 decryptRSA
     * @throws IOException 异常
     */
    public static PrivateKey getPrivateKey(String privateKeyPem) throws IOException {
        PrivateKey privateKey = null;
        Reader privateKeyReader = new StringReader(addPrefixSuffix(privateKeyPem));
        PEMParser privatePemParser = new PEMParser(privateKeyReader);
        Object privateObject = privatePemParser.readObject();
        if (privateObject instanceof PEMKeyPair) {
            PEMKeyPair pemKeyPair = (PEMKeyPair) privateObject;
            if (Security.getProvider(BC) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BC);
            privateKey = converter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
        }
        return privateKey;
    }

    /**
     * 给私钥拼接字符串 -----BEGIN RSA PRIVATE KEY-----\n 和  \n-----END RSA PRIVATE KEY-----
     *
     * @param privateKey 私钥
     * @return
     */
    public static String addPrefixSuffix(String privateKey) {
        if (StringUtils.isBlank(privateKey)) {
            return StringUtils.EMPTY;
        }
        return "-----BEGIN RSA PRIVATE KEY-----\n" + privateKey + "\n-----END RSA PRIVATE KEY-----";
    }

    /**
     * RSA pkcs1 2048bit 解密工具,
     *
     * @param str        被解密的字符串
     * @param privateKey 私钥对象 从 getPrivateKey 获取
     * @return 解密后数据
     * @throws NoSuchPaddingException    异常
     * @throws NoSuchAlgorithmException  异常
     * @throws InvalidKeyException       异常
     * @throws BadPaddingException       异常
     * @throws IllegalBlockSizeException 异常
     */
    public static String decryptRSA(String str, PrivateKey privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding", BC);
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] utf8 = rsa.doFinal(Base64.getDecoder().decode(str));
        return new String(utf8, StandardCharsets.UTF_8);
    }
}
