package com.easyink.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解密注解
 * 用于标识需要对查询结果进行解密处理的方法
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {
    
    /**
     * 是否启用解密
     * @return true表示启用解密处理
     */
    boolean value() default true;
}