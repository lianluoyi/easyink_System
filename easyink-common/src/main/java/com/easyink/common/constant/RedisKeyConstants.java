package com.easyink.common.constant;

/**
 * 类名： RedisKeyConstants
 *
 * @author 佚名
 * @date 2021/9/1 20:23
 */
public class RedisKeyConstants {
    /**
     * 首页数据概览基础数据，redis对应的key
     */
    public static final String CORP_BASIC_DATA = "getCorpBasicData-new2:";
    /**
     * 首页数据概览实时数据，redis对应的key
     */
    public static final String CORP_REAL_TIME = "getCorpRealTimeData-new2:";

    /**
     * 账号强退原因记录 键值
     */
    public static final String ACCOUNT_LOGOUT_REASON_KEY = "accountLogoutReason:";

    /**
     * 拉取离职员工数据 过期KEY
     */
    public static final String DELETE_USER_KEY = "deleteUserKey:";

    private RedisKeyConstants() {

    }
}
