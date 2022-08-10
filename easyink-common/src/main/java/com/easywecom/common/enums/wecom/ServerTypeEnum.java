package com.easywecom.common.enums.wecom;

/**
 * 应用类型枚举
 *
 * @author tigger
 * 2022/2/10 9:49
 **/
public enum ServerTypeEnum {
    /**三方*/
    THIRD("third"),
    /**自建*/
    INTERNAL("internal");


    private String type;

    ServerTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
