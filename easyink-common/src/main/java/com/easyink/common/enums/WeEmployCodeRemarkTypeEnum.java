package com.easyink.common.enums;

import lombok.Getter;

/**
 * 类名：WeEmployCodeRemarkTypeEnum 员工活码设置备注类型枚举值说明
 *
 * @author Society my sister Li
 * @date 2021-11-02 13:50
 */
public enum WeEmployCodeRemarkTypeEnum {

    //不设置
    NO(0),
    //在昵称前设置备注
    BEFORT_NICKNAME(1),
    //在昵称后设置备注
    AFTER_NICKNAME(2);
    @Getter
    private Integer remarkType;

    WeEmployCodeRemarkTypeEnum(Integer remarkType) {
        this.remarkType = remarkType;
    }

}
