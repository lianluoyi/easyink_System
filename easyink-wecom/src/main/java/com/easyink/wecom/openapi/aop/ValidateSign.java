package com.easyink.wecom.openapi.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类名: 校验签名注解
 *
 * @author : silver_chariot
 * @date : 2022/3/14 15:56
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateSign {
}
