package com.easyink.common.utils.wecom;

import cn.hutool.core.util.RandomUtil;
import com.easyink.common.exception.AesException;
import com.easyink.common.exception.wecom.WeComException;
import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.RandomUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.security.SecureRandom;
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
     * 补位
     *
     * @param plaintext
     * @return
     */
    private static String padPlaintext(String plaintext) {
        int blockSize = 16;
        int paddingLength = blockSize - (plaintext.length() % blockSize);

        StringBuilder paddedPlaintext = new StringBuilder(plaintext);
        for (int i = 0; i < paddingLength; i++) {
            paddedPlaintext.append((char)paddingLength);
        }

        return paddedPlaintext.toString();
    }

    /**
     * 加密aes
     *
     * @param plaintext 明文
     * @return
     */
    public String encryptAES(String plaintext) {
        byte[] encryptedBytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec secretKey = new SecretKeySpec(this.aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(this.aesKey, 0, 16));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            encryptedBytes = cipher.doFinal(padPlaintext(plaintext).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("[aes加密]异常,text:{},e:{}", plaintext, ExceptionUtils.getStackTrace(e));
        }
        return Base64.encodeBase64String(encryptedBytes);

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

    /**
     * 生成签名
     *
     * @param timeStamp
     * @param nonce
     * @param randomStr
     * @return
     */
    public String encryptSign(String timeStamp, String nonce, String randomStr) {
        return SHA1.getSHA1(this.token, timeStamp, nonce, randomStr);
    }

    /**
     * 生成echoStr
     * @param corpId 企业id
     * @return
     */
    public String genEchoStr(String text ,String corpId) {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = text.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] receiveidBytes = corpId.getBytes(CHARSET);
        // randomStr + networkBytesOrder + text + receiveid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(receiveidBytes);
        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);
        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();
        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);
            // 使用BASE64对加密后的字符串进行编码
            String base64Encrypted = Base64.encodeBase64String(encrypted);
            return base64Encrypted;
        } catch (Exception e) {
            log.error("[生成echoStr]生成异常,corpId:{}, e:{}", corpId, ExceptionUtils.getStackTrace(e));
        }
        return StringUtils.EMPTY;
    }

    // 生成4个字节的网络字节序
    byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }


    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
