package com.easyink.common.enums;

/**
 * 欢迎语特殊规则类型枚举
 *
 * @author tigger
 * 2022/1/11 16:23
 **/
public enum WelcomeMsgRuleTypeEnum {
    WEEKEND(1);

    private Integer type;


    WelcomeMsgRuleTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
