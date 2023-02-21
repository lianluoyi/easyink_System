package com.easyink.wecom.domain.enums.form;

import com.easyink.common.exception.CustomException;
import lombok.Getter;

import java.util.Optional;

/**
 * 表单分组所属类别
 *
 * @author tigger
 * 2023/1/9 15:30
 **/
public enum FormSourceType {
    /**
     * 企业表单
     */
    CORP(1),
    /**
     * 部门表单
     */
    DEPARTMENT(2),
    /**
     * 个人表单
     */
    PERSONAL(3),
    ;

    @Getter
    private final Integer code;

    FormSourceType(Integer code) {
        this.code = code;
    }


    /**
     * 校验or 返回
     *
     * @param code code
     * @return 枚举
     */
    public static FormSourceType validCode(Integer code) {
        Optional<FormSourceType> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new CustomException("表单分组所属类型异常"));
    }

    public static Optional<FormSourceType> getByCode(Integer code) {
        for (FormSourceType value : values()) {
            if (value.code.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
