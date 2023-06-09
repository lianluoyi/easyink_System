package com.easyink.common.enums;

import lombok.Getter;

/**
 * 区分接口调用来源
 *
 * @author Society my sister Li
 * @date 2021/9/22
 */
public enum CallSourceEnum {

    //系统调用
    SYSTEM("0"),

    //客户端调用
    H5("1");

    @Getter
    private String source;

    CallSourceEnum(String source) {
        this.source = source;
    }

}
