package com.easywecom.common.enums;

import lombok.Getter;

/**
 * 类名：WeWordsCategoryTypeEnum 话术库类型枚举类
 *
 * @author Society my sister Li
 * @date 2021-10-25 10:56
 */
public enum WeWordsCategoryTypeEnum {

    //企业
    CORP(0),
    //部门
    DEPARTMENT(1),
    //个人
    SELF(2);
    @Getter
    private Integer type;

    WeWordsCategoryTypeEnum(Integer type) {
        this.type = type;
    }
}
