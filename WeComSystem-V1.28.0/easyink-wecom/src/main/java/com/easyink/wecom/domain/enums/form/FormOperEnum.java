package com.easyink.wecom.domain.enums.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 表单操作枚举
 *
 * @author wx
 * 2023/1/15 10:35
 **/
@AllArgsConstructor
public enum FormOperEnum {
    UNKNOWN(0, "未知操作"),
    CLICK(1, "点击表单"),
    COMMIT(2, "提交表单"),
    ;
    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    /**
     * 根据code获取表单渠道
     *
     * @param code  类型
     * @return  FormChannelEnum
     */
    public static FormOperEnum getByCode(Integer code) {
        if(code == null) {
            return UNKNOWN;
        }
        return Arrays.stream(values()).filter(a -> code.equals(a.getCode())).findFirst().orElse(UNKNOWN);
    }
}
