package com.easywecom.common.enums.autotag;

import lombok.Getter;

/**
 * 标签类型
 *
 * @author tigger
 * 2022/2/27 20:15
 **/
public enum AutoTagLabelTypeEnum {
    /**关键词*/
    KEYWORD(1),
    /**群*/
    GROUP(2),
    /**新客*/
    CUSTOMER(3),
    ;


    @Getter
    private Integer type;

    AutoTagLabelTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean existType(Integer type) {
        for (AutoTagLabelTypeEnum value : AutoTagLabelTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
