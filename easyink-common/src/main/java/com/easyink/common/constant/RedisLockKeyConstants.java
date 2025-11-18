package com.easyink.common.constant;

/**
 * redis锁key常量
 * @author tigger
 * 2024/12/11 9:47
 **/
public class RedisLockKeyConstants {

    /**
     * 同步客户key前缀
     */
    private static final String SYNC_WE_CUSTOMER_PREFIX = "syncWeCustomer:";

    private RedisLockKeyConstants() {

    }

    /**
     * 获取同步客户锁key
     * @param corpId 企业id
     * @return key
     */
    public static String getSyncCustomerKey(String corpId) {
        return SYNC_WE_CUSTOMER_PREFIX + corpId;
    }
}
