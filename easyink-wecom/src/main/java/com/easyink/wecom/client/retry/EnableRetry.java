package com.easyink.wecom.client.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类名: 重试接口 (由于forest高版本的重定向存在问题,低版本又不支持重试,所以只能自定义重试接口 )
 *
 * @author : silver_chariot
 * @date : 2023/6/25 15:33
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableRetry {
    /**
     * 最大请求次数，默认为4次
     *
     * @return
     */
    int maxAttempts() default 3;

    /**
     * 重试间隔 (单位 ms )默认 1000 ms
     *
     * @return
     */
    long retryInterval() default 1000L;

}
