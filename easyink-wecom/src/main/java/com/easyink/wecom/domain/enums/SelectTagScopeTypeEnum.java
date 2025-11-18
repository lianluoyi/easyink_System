package com.easyink.wecom.domain.enums;

import lombok.Getter;

import java.util.Optional;

/**
 * 选择标签范围类型
 *
 * @author tigger
 * 2025/5/6 16:18
 **/
@Getter
public enum SelectTagScopeTypeEnum {
    /**
     * 标签
     */
    TAG(1),
    /**
     * 标签组
     */
    TAG_GROUP(2),
    ;
    private final Integer code;

    SelectTagScopeTypeEnum(Integer code) {
        this.code = code;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<SelectTagScopeTypeEnum> getByCode(Integer code) {
        for (SelectTagScopeTypeEnum value : values()) {
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
    public static SelectTagScopeTypeEnum validCode(Integer code) {
        Optional<SelectTagScopeTypeEnum> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("选择标签范围类型异常"));
    }
}
