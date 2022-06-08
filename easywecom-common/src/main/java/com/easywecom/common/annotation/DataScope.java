package com.easywecom.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author admin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 用户表的别名
     */
    String userAlias() default "wu";
}
