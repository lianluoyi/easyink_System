package com.easywecom.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名: RoleTypeEnum
 *
 * @author : silver_chariot
 * @date : 2021/9/22 15:40
 */
@AllArgsConstructor
public enum RoleTypeEnum {
    /**
     * 系统默认超级管理员角色
     */
    SYS_ADMIN(1),
    /**
     * 系统默认角色
     */
    SYS_DEFAULT(2),
    /**
     * 用户自定义角色
     */
    CUSTOM(3)
    ;
    /**
     * 角色类型
     */
    @Getter
    private final Integer type;
}
