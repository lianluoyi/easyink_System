package com.easywecom.common.enums;

import lombok.Getter;

/**
 * 标签建群-发送范围 枚举值说明
 *
 * @author Society my sister Li
 * @date 2021/9/14
 */
public enum PresTagGroupTaskSendScopeEnum {

    //0: 全部客户
    ALL(0),

    //1：部分客户
    SOME(1);

    @Getter
    private final Integer sendScope;

    PresTagGroupTaskSendScopeEnum(Integer sendScope) {
        this.sendScope = sendScope;
    }


}
