package com.easyink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： 快递公司名称枚举
 *
 * @author 佚名
 * @date 2021/12/13 16:47
 */
@AllArgsConstructor
@Getter
public enum ExpressNameEnum {
    /**
     * 无
     */
    DEFAULT(0, "", "无"),
    /**
     * 圆通
     */
    YUAN_TONG(1, "yuantong", "圆通速递"),
    /**
     * 韵达
     */
    YUN_DA(2, "yunda", "韵达快递"),
    /**
     * 中通
     */
    ZHONG_TONG(3, "zhongtong", "中通快递"),
    /**
     * 极兔
     */
    JI_TU(4, "jitu", "极兔快递"),

    /**
     * 申通快递
     */
    SHEN_TONG(5, "shentong", "申通快递"),

    /**
     * 跨越速递
     */
    KUA_YUE(6, "kuayue", "跨越速递"),
    ;
    /**
     * 状态码
     */
    public final Integer code;
    /**
     * 含义
     */
    private final String desc;
    /**
     * 公司名称
     */
    private final String name;

    /**
     * 通过状态码获取
     */
    public static ExpressNameEnum getEnum(Integer code) {
        for (ExpressNameEnum expressNameEnum : values()) {
            if (expressNameEnum.getCode().equals(code)) {
                return expressNameEnum;
            }
        }
        return DEFAULT;
    }

    /**
     * 通过名称获取
     */
    public static ExpressNameEnum getEnum(String name) {
        for (ExpressNameEnum expressNameEnum : values()) {
            if (expressNameEnum.getName().equals(name)) {
                return expressNameEnum;
            }
        }
        return DEFAULT;
    }
}
