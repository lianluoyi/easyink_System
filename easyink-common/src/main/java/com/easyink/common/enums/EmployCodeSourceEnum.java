package com.easyink.common.enums;

import lombok.Getter;

/**
 * 员工活码来源类型枚举值说明
 *
 * @author Society my sister Li
 * @date 2021/9/14
 */
public enum EmployCodeSourceEnum {

    //活码创建
    CODE_CREATE(0),

    //新客建群
    NEW_GROUP(1),

    ;

    @Getter
    private final Integer source;

    EmployCodeSourceEnum(Integer source) {
        this.source = source;
    }

}
