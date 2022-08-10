package com.easyink.wecom.strategy.appstrategy;

import com.easyink.common.enums.AppIdEnum;
import com.easyink.common.utils.spring.SpringUtils;

/**
 * 类名： AppConfigHandler
 *
 * @author 佚名
 * @date 2021/12/13 18:01
 */

public class AppConfigHandler {

    public static String switchHandler(Integer appId, String config, String corpId) {
        String result = config;
        switch (AppIdEnum.getEnum(appId)) {
            case YIGE_ORDER:
                result = SpringUtils.getBean(YiGeOrderAppConfigStrategy.class).configHandler(config, corpId);
                break;
            default:
                break;
        }
        return result;
    }
}
