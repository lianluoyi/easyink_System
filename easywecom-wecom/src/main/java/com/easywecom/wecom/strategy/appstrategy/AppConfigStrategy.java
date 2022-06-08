package com.easywecom.wecom.strategy.appstrategy;

/**
 * 类名： AppConfigStrategy
 *
 * @author 佚名
 * @date 2021/12/13 17:06
 */
public interface AppConfigStrategy {
    /**
     * 配置处理
     *
     * @param config 配置（jsonStr）
     * @param corpId 企业id
     * @return 处理后的配置
     */
    String configHandler(String config, String corpId);
}
