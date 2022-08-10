package com.easyink.common.enums;

import lombok.Getter;

/**
 * 类名: BatchUpdateUserInfoTypeEnum 批量修改员工信息的类型枚举值定义
 *
 * @author Society my sister Li
 * @date 2021-11-16 17:14
 */
public enum BatchUpdateUserInfoTypeEnum {

    //角色
    ROLE(1),
    //职务
    POSITION(2),
    //所在部门
    DEPARTMENT(3);
    @Getter
    private Integer type;

    BatchUpdateUserInfoTypeEnum(Integer type) {
        this.type = type;
    }
}
