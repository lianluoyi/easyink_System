package com.easyink.wecom.service;

/**
 * 类名: WeInitService
 *
 * @author: 1*+
 * @date: 2021-09-23 14:16
 */
public interface WeInitService {


    /**
     * 初始化企业配置
     *
     * @param corpId        企业ID
     * @param corpFullName  企业全称
     * @param isCustomApp 是代开发自建
     */
    void initCorpConfig(String corpId, String corpFullName, boolean isCustomApp);

    /**
     * 同步初始化企业配置
     *
     * @param corpId       企业ID
     * @param corpFullName 企业全称
     * @param isCustomApp  是代开发自建
     */
    void initCorpConfigSynchronization(String corpId, String corpFullName, boolean isCustomApp);

    /**
     * 初始化系统默认配置
     *
     * @param corpId       企业id (不可为空)
     * @param corpFullName 企业全名
     */
    void initDefaultSystemProperty(String corpId, String corpFullName);

    /**
     * 同步初始化企业配置
     *
     * @param corpId       企业ID
     * @param corpFullName 企业全称
     */
    void initCorpConfigSynchronization(String corpId, String corpFullName);
}
