package com.easyink.common.enums.code;

import lombok.extern.slf4j.Slf4j;


/**
 * ClassName： welcomeMsgTypeEnum
 *
 * @author wx
 * @date 2022/7/11 14:46
 */
@Slf4j
public enum WelcomeMsgTypeEnum {


    /**
     * 员工/新客 发送普通欢迎语 0
     */
    COMMON_WELCOME_MSG_TYPE(0),
    /**
     * 员工/新客 发送兑换码欢迎语1
     */
    REDEEM_CODE_WELCOME_MSG_TYPE(1),
    ;


    private Integer type;


    WelcomeMsgTypeEnum(Integer type) {
        this.type = type;
    }


    public Integer getType() {
        return type;
    }


}
