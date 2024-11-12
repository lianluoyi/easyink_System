package com.easyink.wecom.utils;

import static com.easyink.common.constant.WeConstans.EXTERNAL_USER_ID_PREFIX;
import static com.easyink.common.constant.WeConstans.USER_ID_PREFIX;

/**
 *
 * @author tigger
 * 2024/11/5 20:47
 **/
public class WeUserUtils {
    private WeUserUtils() {
    }

    /**
     * 判断是否是加密的客户id
     *
     * @param openExternalUserId 加密的客户id
     * @return true or false
     */
    public static boolean isOpenExternalUserId(String openExternalUserId) {
        return openExternalUserId.startsWith(USER_ID_PREFIX) || openExternalUserId.startsWith(EXTERNAL_USER_ID_PREFIX);
    }
}
