package com.easyink.wecom.domain.enums.form;

import lombok.Getter;

import java.util.Optional;

/**
 * 表单项推送类型枚举
 *
 * @author tigger
 * 2025/8/27 20:14
 **/
public enum FormItemPushTypeEnum {
    /**
     * 是否满意
     */
    IS_SATISFIED(1),
    /**
     * 不满意原因
     */
    DISSATISFACTION_REASON(2),
    /**
     * 服务建议
     */
    SERVICE_SUGGESTION(3),
    ;
    @Getter
    private final Integer code;

    FormItemPushTypeEnum(Integer code) {
        this.code = code;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<FormItemPushTypeEnum> getByCode(Integer code) {
        for (FormItemPushTypeEnum value : values()) {
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
    public static FormItemPushTypeEnum validCode(Integer code) {
        Optional<FormItemPushTypeEnum> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("表单项推送类型枚举异常"));
    }
}
