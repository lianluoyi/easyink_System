package com.easyink.common.annotation;

import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段加密标识注解
 * 用于标识需要进行加密解密处理的字段
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {
    
    /**
     * 字段类型，用于确定加密策略
     * @return 字段类型
     */
    FieldType value() default FieldType.COMMON;

    /**
     * 敏感字段空字符串时, 加密字段的默认值
     * @return
     */
    String emptyValue() default "";

    /**
     * 原值为null时, 加密字段的默认值
     * @return
     */
    ValueType nullValue()  default ValueType.Null;

    /**
     * 字段类型枚举
     */
    enum FieldType {
        /** 手机号 */
        MOBILE,
        /** 电话号码 */
        TELEPHONE, 
        /** 地址 */
        ADDRESS,
        /** 通用字段 */
        COMMON
    }
    /**
     * 字段类型枚举
     */
    @Getter
    enum ValueType {
        /** 手机号 */
        EMPTY(""),
        /** 电话号码 */
        Null(null),
        ;
        private final String value;

        ValueType(String value) {
            this.value = value;
        }
    }
}