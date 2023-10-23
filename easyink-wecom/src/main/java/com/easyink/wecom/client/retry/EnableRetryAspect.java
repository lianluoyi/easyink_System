package com.easyink.wecom.client.retry;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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

    @Around("@annotation(EnableRetry) || @within(EnableRetry)")
    public Object retryOnCondition(ProceedingJoinPoint joinPoint) throws Throwable {
        EnableRetry retry = getEnableRetry(joinPoint);
        if (retry == null) {
            // 未获取到注解，放行原方法
            return joinPoint.proceed();
        }
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
                // 自定义重试条件判断
                shouldRetry = attemptCount <= maxAttempts && shouldRetry(exception, retry);
                if (shouldRetry) {
                    log.info("[重试]出现异常,进行重试,请求次数:{},e:{}", attemptCount, ExceptionUtils.getMessage(lastException));
                } else {
                    log.info("[重试]不满足条件，不进行重试，异常原因：{}", ExceptionUtils.getStackTrace(exception));
                }
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
     * @return true 表示需要重试，返回 false 表示不需要重试
     */
    private boolean shouldRetry(Throwable exception, EnableRetry retry) {
        // 根据异常类型或其他条件判断是否需要重试
        // 获取注解中所有的异常类属性
        Class<? extends Throwable>[] annotationExClassArr = retry.retryExceptionClass();
        // 如果注解中未指定异常类，直接重试
        if (ArrayUtils.isEmpty(annotationExClassArr)) {
            return true;
        }
        // 判断是否匹配注解中的异常，若匹配注解中的异常，则进行重试
        return isMarkException(annotationExClassArr, exception);
    }


    /**
     * 从连接点中获取注解信息
     *
     * @param joinPoint {@link ProceedingJoinPoint}
     * @return {@link EnableRetry}
     */
    private EnableRetry getEnableRetry(ProceedingJoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 方法上的注解
        EnableRetry methodAnnotation = method.getAnnotation(EnableRetry.class);
        EnableRetry classAnnotation = null;
        Annotation annotation = signature.getDeclaringType().getAnnotation(EnableRetry.class);
        if (annotation != null) {
            classAnnotation = (EnableRetry) annotation;
        }
        // 若方法和类上都有注解，优先使用方法上的注解
        if (methodAnnotation != null && classAnnotation != null) {
            return methodAnnotation;
        }
        return methodAnnotation == null ? classAnnotation : methodAnnotation;
    }

    /**
     * 判断抛出的异常是否与注解中异常类匹配
     *
     * @param annotationExClassArr 注解中的异常数组
     * @param exception            {@link Throwable}
     * @return true 匹配，false 不匹配
     */
    private boolean isMarkException(Class<? extends Throwable>[] annotationExClassArr, Throwable exception) {
        // 获取抛出的异常类属性
        Class<? extends Throwable> exceptionClass = exception.getClass();
        for (Class<? extends Throwable> annotationClass : annotationExClassArr) {
            // 只要抛出的异常是注解中异常的同类或是其子类，都算匹配
            if (annotationClass.isAssignableFrom(exceptionClass)) {
                return true;
            }
        }
        return false;
    }
}
