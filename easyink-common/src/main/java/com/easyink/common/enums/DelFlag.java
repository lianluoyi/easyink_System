package com.easyink.common.enums;

import lombok.Getter;

import java.util.Optional;

/**
 * 删除标识
 *
 * @author tigger
 * 2023/1/9 16:33
 **/
public enum DelFlag {
    /**
     * 未知
     */
    DEL(1),
    UN_DEL(0),
    ;
    @Getter
    private final Integer code;

    DelFlag(Integer code) {
        this.code = code;
    }


    public static Optional<DelFlag> getByCode(Integer code) {
        for (DelFlag value : values()) {
            if (value.code.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
