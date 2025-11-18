package com.easyink.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密注解
 * 用于标识需要对响应数据进行加密处理的方法
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {
    
    /**
     * 是否启用加密
     * @return true表示启用加密处理
     */
    boolean value() default true;
}