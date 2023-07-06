package com.easyink.wecom.client.retry;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 类名: 重试切面 (由于forest高版本的重定向存在问题,低版本又不支持重试,所以只能自定义重试接口 )
 * <p>
 * 如果需要重试 请自行在对应的forest onSuccess 方法里进行判断并 抛出异常
 *
 * @author : silver_chariot
 * @date : 2023/6/25 15:34
 **/
@Aspect
@Component
@Slf4j
public class EnableRetryAspect {

    @Around("@annotation(retry)")
    public Object retryOnCondition(ProceedingJoinPoint joinPoint, EnableRetry retry) throws Throwable {
        int maxAttempts = retry.maxAttempts();
        int attemptCount = 0;
        long retryInterval = retry.retryInterval();
        Throwable lastException;
        boolean shouldRetry;
        do {
            attemptCount++;
            try {
                // 执行原方法
                return joinPoint.proceed();
            } catch (Throwable exception) {
                lastException = exception;
                log.info("[重试]出现异常,进行重试,请求次数:{},e:{}", attemptCount, ExceptionUtils.getMessage(lastException));
                // 自定义重试条件判断
                shouldRetry = attemptCount <= maxAttempts && shouldRetry(exception);
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (shouldRetry);
       // 达到最大重试次数，抛出异常
        throw lastException;
    }

    /**
     * 自定义重试条件判断逻辑
     *
     * @param exception
     * @return
     */
    private boolean shouldRetry(Throwable exception) {
        // 根据异常类型或其他条件判断是否需要重试
        // 返回 true 表示需要重试，返回 false 表示不需要重试
        return true;
    }
}
