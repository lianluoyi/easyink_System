package com.easyink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话发言Enum
 *
 * @author wx
 * 2023/2/20 17:48
 **/
@AllArgsConstructor
public enum ContactSpeakEnum {
    /**
     * 客户先发言
     */
    CUSTOMER(0),
    /**
     * 员工先发言
     */
    USER(1),
    ;
    @Getter
    private Integer code;
}
