package com.easyink.wecom.domain.enums.form;

import com.easyink.common.exception.CustomException;
import lombok.Getter;

import java.util.Optional;

/**
 * 提交次数类型
 *
 * @author tigger
 * 2023/1/10 10:23
 **/
public enum SubmitCntType {
    /**
     * 1: 不限
     */
    NOT_LIMIT(1, "不限"),
    /**
     * 2:每个客户限提交1次
     */
    PER_ONCE(2, "每个客户限提交一次"),
    ;
    @Getter
    private final Integer code;
    @Getter
    private final String dict;

    SubmitCntType(Integer code, String dict) {
        this.code = code;
        this.dict = dict;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<SubmitCntType> getByCode(Integer code) {
        for (SubmitCntType value : values()) {
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
    public static SubmitCntType validCode(Integer code) {
        Optional<SubmitCntType> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new CustomException("提交次数类型异常"));
    }
}
