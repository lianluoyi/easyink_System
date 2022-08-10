package com.easyink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： 应用id枚举
 *
 * @author 佚名
 * @date 2021/12/13 18:05
 */
@Getter
@AllArgsConstructor
public enum AppIdEnum {
    /**
     * 无应用
     */
    NO_APP(0, "无应用"),
    /**
     * 企微plus
     */
    WECHAT_SERVICE_PLUS(1, "企微plus"),
    /**
     * 韵达
     */
    YIGE_ORDER(2, "壹鸽快递工单助手"),
    ;
    /**
     * 状态码
     */
    public final Integer code;
    /**
     * 含义
     */
    private final String desc;

    public static AppIdEnum getEnum(Integer code) {
        for (AppIdEnum appIdEnum : values()) {
            if (appIdEnum.getCode().equals(code)) {
                return appIdEnum;
            }
        }
        return NO_APP;
    }
}
