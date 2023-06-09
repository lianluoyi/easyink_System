package com.easyink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名: 客户状态枚举
 *
 * @author : silver_chariot
 * @date : 2021/11/23 10:48
 */
@AllArgsConstructor
@Getter
public enum CustomerStatusEnum {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 已流失
     */
    DRAIN(1),
    /**
     * 员工删除客户
     */
    DELETE(2),
    /**
     * 待继承
     */
    TO_BE_TRANSFERRED(3),
    /**
     * 转接中
     */
    TRANSFERRING(4),
    ;

    private final Integer code;

    /**
     * 是流失 或者被删除的 客户关系
     *
     * @param status 状态
     * @return true or false
     */
    public static boolean isDel(Integer status) {
        if (status == null) {
            return false;
        }
        return DRAIN.getCode().equals(status) || DELETE.getCode().equals(status);
    }
}
