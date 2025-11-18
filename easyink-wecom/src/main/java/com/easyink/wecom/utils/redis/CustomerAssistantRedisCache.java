package com.easyink.wecom.utils.redis;

import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 类名：获客链接Redis工具类
 *
 * @author lichaoyu
 * @date 2023/8/26 18:28
 */
@Component("customerAssistantRedisCache")
public class CustomerAssistantRedisCache extends RedisCache {


    /**
     * 获客链接告警设置
     */
    private static final String WE_EMPLE_CODE_WARN = "weEmpleCodeWarn:";

    /**
     * 链接不可用通知
     */
    private static final String LINK_UNAVAILABLE = "linkUnavailable:";

    /**
     * 链接即将耗尽通知
     */
    private static final String BALANCE_LOW = "balanceLow:";

    /**
     * 链接已耗尽通知
     */
    private static final String BALANCE_EXHAUSTED = "balanceExhausted:";

    /**
     * 获客额度即将过期通知
     */
    private static final String QUOTA_EXPIRE_SOON = "quotaExpireSoon:";

    /**
     * 获客情况
     */
    private static final String WE_EMPLE_CODE_SITUATION = "weEmpleCodeSituation:";

    /**
     * 获客情况-今日新增客户数
     */
    private static final String TODAY_NEW_CUSTOMER_CNT = "todayNewCustomerCnt:";

    /**
     * KEY，间隔符号
     */
    private static final String KEY_SEPARATOR = ":";

    /**
     * 过期时间24H
     */
    private static final Integer KEY_EXPIRE_TIME = 60 * 60 * 24 * 1000;

    /**
     * 获取链接不可用通知REDIS　KEY
     *
     * @param corpId 企业ID
     * @return KEY
     */
    private String getLinkUnavailableKey(String corpId) {
        return WE_EMPLE_CODE_WARN + LINK_UNAVAILABLE + corpId;
    }

    /**
     * 获取链接即将耗尽通知REDIS KEY
     *
     * @param corpId 企业ID
     * @return KEY
     */
    private String getBalanceLowKey(String corpId) {
        return WE_EMPLE_CODE_WARN + BALANCE_LOW + corpId;
    }

    /**
     * 获取链接已耗尽通知Redis KEY
     *
     * @param corpId 企业ID
     * @return KEY
     */
    private String getBalanceExhaustedKey(String corpId) {
        return WE_EMPLE_CODE_WARN + BALANCE_EXHAUSTED + corpId;
    }

    /**
     * 获取获客额度即将过期通知Redis KEY
     *
     * @param corpId 企业ID
     * @return KEY
     */
    private String getQuotaExpireSoonKey(String corpId) {
        return WE_EMPLE_CODE_WARN + QUOTA_EXPIRE_SOON + corpId;
    }

    /**
     * 获取获客情况-今日新增客户数Redis KEY
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     * @return KEY
     */
    private String getWeEmpleCodeSituationKey(String corpId, String date) {
        return WE_EMPLE_CODE_SITUATION + TODAY_NEW_CUSTOMER_CNT + corpId + KEY_SEPARATOR + date;
    }

    /**
     * 获取链接不可用通知Value
     *
     * @param corpId 企业ID
     * @return VALUE
     */
    public String getLinkUnavailableValue(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return StringUtils.EMPTY;
        }
        return getCacheObject(getLinkUnavailableKey(corpId));
    }

    /**
     * 设置链接不可用通知Value
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     */
    public void setLinkUnavailableValue(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        setCacheObject(getLinkUnavailableKey(corpId), date, KEY_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取链接即将耗尽通知Value
     *
     * @param corpId 企业ID
     * @return VALUE
     */
    public String getBalanceLowValue(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return StringUtils.EMPTY;
        }
        return getCacheObject(getBalanceLowKey(corpId));
    }

    /**
     * 设置链接即将耗尽通知Value
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     */
    public void setBalanceLowValue(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        setCacheObject(getBalanceLowKey(corpId), date, KEY_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取链接已经耗尽通知Value
     *
     * @param corpId 企业ID
     * @return VALUE
     */
    public String getBalanceExhaustedValue(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return StringUtils.EMPTY;
        }
        return getCacheObject(getBalanceExhaustedKey(corpId));
    }

    /**
     * 设置链接已经耗尽通知Value
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     */
    public void setBalanceExhaustedValue(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        setCacheObject(getBalanceExhaustedKey(corpId), date, KEY_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取获客额度即将过期通知Value
     *
     * @param corpId 企业ID
     * @return VALUE
     */
    public String getQuotaExpireSoonValue(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return StringUtils.EMPTY;
        }
        return getCacheObject(getQuotaExpireSoonKey(corpId));
    }

    /**
     * 设置获客额度即将过期通知Value
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     */
    public void setQuotaExpireSoonValue(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        setCacheObject(getQuotaExpireSoonKey(corpId), date, KEY_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 新增获客情况-今日新增客户数
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     */
    public void incrementNewCustomerCnt(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        hIncrement(getWeEmpleCodeSituationKey(corpId, date), date, 1);
    }

    /**
     * 获取新增获客情况-今日新增客户数
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     * @return 今日新增客户数
     */
    public Integer getTodayNewCustomerCnt(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return 0;
        }
        return getHashIncrCnt(getWeEmpleCodeSituationKey(corpId, date), date);
    }

    /**
     * 根据日期删除获客情况-今日新增客户数Redis KEY
     *
     * @param corpId
     * @param date
     */
    public void delTodayNewCustomerCnt(String corpId, String date) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        deleteObject(getWeEmpleCodeSituationKey(corpId, date));
    }
}
