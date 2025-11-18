package com.easyink.framework.aspectj;

import com.easyink.common.annotation.Encrypt;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 加密AOP切面类
 * 拦截Controller层方法，对响应数据进行加密处理
 * 支持AjaxResult类型的特殊处理
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class EncryptAop {

    /**
     * 定义切点：拦截所有标注了@Encrypt注解的方法
     */
    @Pointcut("@annotation(com.easyink.common.annotation.Encrypt)")
    public void encryptPointcut() {
    }

    /**
     * 定义切点：拦截所有Controller层的方法
     */
    @Pointcut("execution(* com.easyink..controller..*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：处理@Encrypt注解的方法
     * 
     * @param joinPoint 连接点
     * @param encrypt 加密注解
     * @return 处理结果
     */
    @Around("encryptPointcut() && @annotation(encrypt)")
    public Object aroundEncrypt(ProceedingJoinPoint joinPoint, Encrypt encrypt) throws Throwable {
        // 执行原方法
        Object result = joinPoint.proceed();
        
        if (!encrypt.value() || result == null) {
            // 如果注解设置为不加密或结果为空，直接返回
            return result;
        }

        // 对结果进行加密处理
        try {
            processEncryption(result);
            log.debug("加密处理完成，方法：{}", joinPoint.getSignature().toShortString());
        } catch (Exception e) {
            log.error("加密处理失败，方法：{}，错误：{}", joinPoint.getSignature().toShortString(), e.getMessage());
        }
        
        return result;
    }

    /**
     * 处理加密逻辑，特别支持AjaxResult类型
     * 
     * @param result 待处理的结果对象
     */
    private void processEncryption(Object result) {
        if (result == null) {
            return;
        }

        if (result instanceof AjaxResult) {
            // 特殊处理AjaxResult类型
            processAjaxResult((AjaxResult<?>) result);
        } else {
            // 处理其他类型
            SensitiveFieldProcessor.encrypt(result);
        }
    }

    /**
     * 处理AjaxResult类型的加密
     * 只对data字段中的数据进行加密处理，保持code和msg字段不变
     * 
     * @param ajaxResult AjaxResult对象
     */
    private void processAjaxResult(AjaxResult<?> ajaxResult) {
        if (ajaxResult == null) {
            return;
        }

        // 获取data字段的数据
        Object data = ajaxResult.getData();
        if (data != null) {
            // 对data字段进行加密处理
            SensitiveFieldProcessor.encrypt(data);
            log.debug("AjaxResult的data字段加密处理完成，数据类型：{}", data.getClass().getSimpleName());
        }

        // 处理map中的额外数据（如果有的话）
        if (ajaxResult.getMap() != null && !ajaxResult.getMap().isEmpty()) {
            for (Object value : ajaxResult.getMap().values()) {
                if (value != null) {
                    SensitiveFieldProcessor.encrypt(value);
                }
            }
            log.debug("AjaxResult的map字段加密处理完成");
        }
    }
}