package com.easywecom.common.utils.wecom;

import com.easywecom.common.exception.wecom.WeComException;
import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * <pre>
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * Copyright (c) 1998-2014 Tencent Inc.
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 * </pre>
 *
 * @author admin
 */
@NoArgsConstructor
@Slf4j
public class WxCryptUtil {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final ThreadLocal<DocumentBuilder> BUILDER_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setExpandEntityReferences(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException exc) {
            throw new IllegalArgumentException(exc);
        }
    });

    protected byte[] aesKey;
    protected String token;


    /**
     * 构造函数.
     *
     * @param token          公众平台上，开发者设置的token
     * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
     */
    public WxCryptUtil(String token, String encodingAesKey) {
        this.token = token;
        this.aesKey = BaseEncoding.base64().decode(CharMatcher.whitespace().removeFrom(encodingAesKey));
    }

    private static String extractEncryptPart(String xml) {
        try {
            DocumentBuilder db = BUILDER_LOCAL.get();
            Document document = db.parse(new InputSource(new StringReader(xml)));

            Element root = document.getDocumentElement();
            return root.getElementsByTagName("Encrypt").item(0).getTextContent();
        } catch (Exception e) {
            throw new WeComException(e.getMessage());
        }
    }


    /**
     * 4个字节的网络字节序bytes数组还原成一个数字.
     */
    private static int bytesNetworkOrder2Number(byte[] bytesInNetworkOrder) {
        int sourceNumber = 0;
        int size = 4;
        for (int i = 0; i < size; i++) {
            sourceNumber <<= 8;
            sourceNumber |= bytesInNetworkOrder[i] & 0xff;
        }
        return sourceNumber;
    }


    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取xml中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp    时间戳，对应URL参数的timestamp
     * @param nonce        随机串，对应URL参数的nonce
     * @param encryptedXml 密文，对应POST请求的数据
     * @return 解密后的原文
     */
    public String decrypt(String msgSignature, String timeStamp, String nonce, String encryptedXml) {
        // 密钥，公众账号的app corpSecret
        // 提取密文
        String cipherText = extractEncryptPart(encryptedXml);

        // 验证安全签名
        String signature = SHA1.gen(this.token, timeStamp, nonce, cipherText);
        if (!signature.equals(msgSignature)) {
            throw new WeComException("加密消息签名校验失败");
        }

        // 解密
        return decrypt(cipherText);
    }

    /**
     * 对密文进行解密.
     *
     * @param cipherText 需要解密的密文
     * @return 解密得到的明文
     */
    public String decrypt(String cipherText) {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(this.aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(this.aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(cipherText);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new WeComException(e.getMessage());
        }

        String xmlContent;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和AppId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = bytesNetworkOrder2Number(networkOrder);

            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
        } catch (Exception e) {
            throw new WeComException(e.getMessage());
        }

        return xmlContent;

    }

    public String verifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) throws WeComException {
        String signature = SHA1.getSHA1(this.token, timeStamp, nonce, echoStr);
        log.info("回调配置校验接口,生成的签名:{}", signature);
        if (!signature.equals(msgSignature)) {
            throw new WeComException("签名验证错误");
        } else {
            return this.decrypt(echoStr);
        }
    }
}
