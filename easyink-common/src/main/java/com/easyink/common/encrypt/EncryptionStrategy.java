package com.easyink.common.encrypt;

/**
 * 加密策略接口
 */
public interface EncryptionStrategy {
    /**
     * 加密方法
     * @param original 原始字符串
     * @return 加密后的字符串
     */
    String encrypt(String original);
    
    /**
     * 解密方法
     * @param encrypted 已加密的字符串
     * @return 解密后的字符串
     */
    String decrypt(String encrypted);
}