package com.easywecom.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
/**
 * 类名: DataScopeEnum
 *
 * @author : silver_chariot
 * @date : 2021/8/26 20:33
 */
@AllArgsConstructor
public enum DataScopeEnum {
    /**
     * 全部数据权限
     */
    ALL("1"),

    /**
     * 自定数据权限
     */
    CUSTOM("2"),

    /**
     * 部门数据权限
     */
    SELF_DEPT("3"),

    /**
     * 部门及以下数据权限
     */
    DEPT_AND_CHILD("4"),

    /**
     * 仅本人数据权限
     */
    SELF("5"),
    ;
    @Getter
    private final String code;

    /**
     * 根据CODE获取数据权限
     *
     * @param code
     * @return
     */
    public static DataScopeEnum getDataScope(String code) {
        return Arrays.stream(values()).filter(value -> value.getCode().equals(code)).findFirst().orElse(null);
    }
}
