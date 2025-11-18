package com.easyink.framework.aspectj;

import com.easyink.common.annotation.Decrypt;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 解密AOP切面类
 * 拦截标注了@Decrypt注解的方法，对查询结果进行解密处理
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DecryptAop {

    /**
     * 定义切点：拦截所有标注了@Decrypt注解的方法
     */
    @Pointcut("@annotation(com.easyink.common.annotation.Decrypt)")
    public void decryptPointcut() {
    }

    /**
     * 定义切点：拦截所有Mapper接口的查询方法
     */
    @Pointcut("execution(* com.easyink..mapper..*(..))")
    public void mapperPointcut() {
    }

    /**
     * 定义切点：拦截所有Service接口的查询方法
     */
    @Pointcut("execution(* com.easyink..service..select*(..)) || " +
              "execution(* com.easyink..service..get*(..)) || " +
              "execution(* com.easyink..service..find*(..)) || " +
              "execution(* com.easyink..service..list*(..)) || " +
              "execution(* com.easyink..service..query*(..))")
    public void serviceQueryPointcut() {
    }

    /**
     * 环绕通知：处理@Decrypt注解的方法
     * 
     * @param joinPoint 连接点
     * @param decrypt 解密注解
     * @return 处理结果
     */
    @Around("decryptPointcut() && @annotation(decrypt)")
    public Object aroundDecrypt(ProceedingJoinPoint joinPoint, Decrypt decrypt) throws Throwable {
        if (!decrypt.value()) {
            // 如果注解设置为不解密，直接执行原方法
            return joinPoint.proceed();
        }

        // 执行原方法
        Object result = joinPoint.proceed();
        
        // 对结果进行解密处理
        if (result != null) {
            try {
                SensitiveFieldProcessor.decrypt(result);
                log.debug("解密处理完成，方法：{}", joinPoint.getSignature().toShortString());
            } catch (Exception e) {
                log.error("解密处理失败，方法：{}，错误：{}", joinPoint.getSignature().toShortString(), e.getMessage());
            }
        }
        
        return result;
    }

    /**
     * 环绕通知：处理Mapper查询方法
     * 
     * @param joinPoint 连接点
     * @return 处理结果
     */
    @Around("mapperPointcut()")
    public Object aroundMapper(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行原方法
        Object result = joinPoint.proceed();
        
        // 对结果进行解密处理
        if (result != null) {
            try {
                SensitiveFieldProcessor.decrypt(result);
                log.debug("Mapper解密处理完成，方法：{}", joinPoint.getSignature().toShortString());
            } catch (Exception e) {
                log.error("Mapper解密处理失败，方法：{}，错误：{}", joinPoint.getSignature().toShortString(), e.getMessage());
            }
        }
        
        return result;
    }
}