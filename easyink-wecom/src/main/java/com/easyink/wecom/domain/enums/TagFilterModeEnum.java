package com.easyink.wecom.domain.enums;

import lombok.Getter;

import java.util.Optional;

/**
 * 标签过滤模式枚举
 * @author tigger
 * 2024/11/26 17:10
 **/
@Getter
public enum TagFilterModeEnum {
    /**
     * 满足所有
     */
    ALL(1),
    /**
     * 满足任一
     */
    ONE_OF(2),
    ;
    private final Integer code;

    TagFilterModeEnum(Integer code) {
        this.code = code;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<TagFilterModeEnum> getByCode(Integer code) {
        for (TagFilterModeEnum value : values()) {
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
    public static TagFilterModeEnum validCode(Integer code) {
        Optional<TagFilterModeEnum> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("标签过滤模式枚举异常"));
    }
}
