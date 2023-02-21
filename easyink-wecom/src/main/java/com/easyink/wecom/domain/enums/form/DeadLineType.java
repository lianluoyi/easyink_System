package com.easyink.wecom.domain.enums.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import lombok.Getter;

import java.util.Optional;

/**
 * 截止时间类型
 *
 * @author tigger
 * 2023/1/10 10:08
 **/
public enum DeadLineType {
    /**
     * 永久
     */
    FOREVER(1,"永久有效"),
    /**
     * 自定义
     */
    CUSTOM(2,"自定义日期"),
    ;
    @Getter
    private final Integer code;
    @Getter
    private final String dict;

    DeadLineType(Integer code, String dict) {
        this.code = code;
        this.dict = dict;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<DeadLineType> getByCode(Integer code) {
        for (DeadLineType value : values()) {
            if (value.code.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 校验or 返回
     *
     * @param code code
     * @return 枚举
     */
    public static DeadLineType validCode(Integer code) {
        Optional<DeadLineType> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new CustomException("截止时间类型异常"));
    }
}
