package com.easyink.common.encrypt;

import cn.hutool.extra.spring.SpringUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 基于策略模式的加解密工具类
 * 结合EncryptionStrategy接口实现，支持可扩展的加密策略
 */
public class StrategyCryptoUtil {


    /**
     * 使用默认策略加密
     *
     * @param content 待加密内容
     * @return 加密后的字符串
     */
    public static String esensitization(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        StringBuilder sb = new StringBuilder(content);
        if(PatternRuleUtil.mobilePhone(sb)){
            return sb.toString();
        }
        PatternRuleUtil.address(sb);
        PatternRuleUtil.addressExt(sb);
        return sb.toString();
    }
    /**
     * 使用脱敏地址
     *
     * @param content 待加密内容
     * @return 加密后的字符串
     */
    public static String esensitizationAllAddress(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        return "***";
    }

    /**
     * 使用默认策略加密
     *getAddContactRecord
     * @param content 待加密内容
     * @return 加密后的字符串
     */
    public static String encrypt(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        return SpringUtil.getBean(EncryptionStrategy.class).encrypt(content);
    }

    /**
     * 使用默认策略解密
     *
     * @param encryptedContent 已加密内容
     * @return 解密后的字符串
     */
    public static String decrypt(String encryptedContent) {
        if (encryptedContent == null || encryptedContent.isEmpty()) {
            return encryptedContent;
        }
        return SpringUtil.getBean(EncryptionStrategy.class).decrypt(encryptedContent);
    }



    /**
     * 使用指定策略解密
     *
     * @param encryptedContent 已加密内容
     * @param strategy         解密策略
     * @return 解密后的字符串
     */
    public static String decrypt(String encryptedContent, EncryptionStrategy strategy) {
        if (encryptedContent == null || encryptedContent.isEmpty()) {
            return encryptedContent;
        }
        return strategy.decrypt(encryptedContent);
    }

    /**
     * MD5摘要
     *
     * @param content 待摘要内容
     * @return MD5摘要字符串(32位小写)
     */
    public static String md5(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5摘要失败: " + e.getMessage(), e);
        }
    }

    /**
     * SHA256摘要
     *
     * @param content 待摘要内容
     * @return SHA256摘要字符串(64位小写)
     */
    public static String sha256(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA256摘要失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Base64编码
     *
     * @param content 待编码内容
     * @return Base64编码后的字符串
     */
    public static String encodeBase64(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     *
     * @param encodedContent 已编码内容
     * @return 解码后的字符串
     */
    public static String decodeBase64(String encodedContent) {
        return new String(Base64.getDecoder().decode(encodedContent), StandardCharsets.UTF_8);
    }
}