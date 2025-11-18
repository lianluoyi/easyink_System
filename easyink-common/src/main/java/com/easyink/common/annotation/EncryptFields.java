package com.easyink.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段集合标识注解
 * 用于标识复杂对象中是否需要对内部字段进行嵌套加密解密处理
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptFields {
    
    /**
     * 是否启用嵌套处理
     * @return true表示需要对内部字段进行嵌套处理
     */
    boolean value() default true;
}