package com.easyink.wecom.domain.enums.form;

import lombok.Getter;

import java.util.Optional;

/**
 * 发送来源类型
 *
 * @author tigger
 * 2025/8/27 9:42
 **/
public enum SendSourceTypeEnum {
    /**
     * 欢迎语
     */
    WELCOME_MSG(1),
    /**
     * 侧边栏
     */
    SIDEBAR(2),
    ;
    @Getter
    private final Integer code;

    SendSourceTypeEnum(Integer code) {
        this.code = code;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<SendSourceTypeEnum> getByCode(Integer code) {
        for (SendSourceTypeEnum value : values()) {
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
    public static SendSourceTypeEnum validCode(Integer code) {
        Optional<SendSourceTypeEnum> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("发送来源类型异常"));
    }
}
