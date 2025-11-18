package com.easyink.quartz.util;

import cn.hutool.core.collection.CollUtil;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.quartz.domain.SysJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * 任务执行工具
 *
 * @author admin
 */
public class JobInvokeUtil {
    private static final Logger log = LoggerFactory.getLogger(JobInvokeUtil.class);
    
    private JobInvokeUtil(){
    }
    
    /**
     * 允许执行的任务类包名
     * 只允许执行com.easyink.quartz.task包下的类，防止恶意类加载
     */
    private static final Set<String> ALLOWED_PACKAGES = new HashSet<>();
    
    static {
        // 只允许com.easyink.quartz.task包下的类
        ALLOWED_PACKAGES.add("com.easyink.quartz.task");
    }
    
    /**
     * 执行方法
     *
     * @param sysJob 系统任务
     */
    public static void invokeMethod(SysJob sysJob) throws Exception {
        String invokeTarget = sysJob.getInvokeTarget();
        
        // 记录任务开始执行
        log.info("开始执行定时任务: {} -> {}", sysJob.getJobName(), invokeTarget);
        
        try {
            // 1. 基础安全检查
            if (invokeTarget == null || invokeTarget.trim().isEmpty()) {
                throw new SecurityException("调用目标不能为空");
            }
            
            // 2. 检查是否包含危险的关键字
            if (containsDangerousKeywords(invokeTarget)) {
                String errorMsg = String.format("检测到危险关键字，拒绝执行: %s", invokeTarget);
                log.error(errorMsg);
                throw new SecurityException(errorMsg);
            }
            
            String beanName = getBeanName(invokeTarget);
            String methodName = getMethodName(invokeTarget);
            List<Object[]> methodParams = getMethodParams(invokeTarget);

            log.info("执行定时任务: {} -> {}.{}", sysJob.getJobName(), beanName, methodName);

            if (!isValidClassName(beanName)) {
                // Spring Bean调用，也需要进行包名校验
                Object bean = SpringUtils.getBean(beanName);
                
                // 检查Bean的类是否在允许的包中
                String className = bean.getClass().getName();
                if (!isAllowedClass(className)) {
                    String errorMsg = String.format("不允许执行Bean: %s，其类 %s 不在允许的包中", beanName, className);
                    log.error(errorMsg);
                    throw new SecurityException(errorMsg);
                }
                
                log.info("执行Spring Bean调用任务: {} -> {}", beanName, className);
                invokeMethod(bean, methodName, methodParams);
            } else {
                // 类名调用，进行包名安全检查
                if (!isAllowedClass(beanName)) {
                    String errorMsg = String.format("不允许执行类: %s，该类不在允许的包中", beanName);
                    log.error(errorMsg);
                    throw new SecurityException(errorMsg);
                }
                
                log.info("执行类名调用任务: {}", beanName);
                
                // 使用安全的反射执行（已通过安全检查）
                Object bean = invokeMethodSafely(beanName);
                invokeMethod(bean, methodName, methodParams);
            }
            
            // 记录任务执行成功
            log.info("定时任务执行成功: {}", sysJob.getJobName());
            
        } catch (Exception e) {
            // 记录任务执行失败
            log.error("定时任务执行失败: {} -> {}", sysJob.getJobName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 检查类是否在允许的包中
     *
     * @param className 类名
     * @return true表示允许，false表示不允许
     */
    private static boolean isAllowedClass(String className) {
        if (className == null || className.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否在允许的包中
        for (String allowedPackage : ALLOWED_PACKAGES) {
            if (className.startsWith(allowedPackage + ".")) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 调用任务方法
     *
     * @param bean         目标对象
     * @param methodName   方法名称
     * @param methodParams 方法参数
     */
    private static void invokeMethod(Object bean, String methodName, List<Object[]> methodParams)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        if (CollUtil.isNotEmpty(methodParams)) {
            Method method = bean.getClass().getDeclaredMethod(methodName, getMethodParamsType(methodParams));
            method.invoke(bean, getMethodParamsValue(methodParams));
        } else {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.invoke(bean);
        }
    }

    /**
     * 校验是否为class包名
     *
     * @param invokeTarget 名称
     * @return true是 false否
     */
    public static boolean isValidClassName(String invokeTarget) {
        return org.apache.commons.lang3.StringUtils.countMatches(invokeTarget, ".") > 1;
    }

    /**
     * 获取bean名称
     *
     * @param invokeTarget 目标字符串
     * @return bean名称
     */
    public static String getBeanName(String invokeTarget) {
        String beanName = org.apache.commons.lang3.StringUtils.substringBefore(invokeTarget, "(");
        return org.apache.commons.lang3.StringUtils.substringBeforeLast(beanName, ".");
    }

    /**
     * 获取bean方法
     *
     * @param invokeTarget 目标字符串
     * @return method方法
     */
    public static String getMethodName(String invokeTarget) {
        String methodName = org.apache.commons.lang3.StringUtils.substringBefore(invokeTarget, "(");
        return org.apache.commons.lang3.StringUtils.substringAfterLast(methodName, ".");
    }

    /**
     * 获取method方法参数相关列表
     *
     * @param invokeTarget 目标字符串
     * @return method方法相关参数列表
     */
    public static List<Object[]> getMethodParams(String invokeTarget) {
        String methodStr = org.apache.commons.lang3.StringUtils.substringBetween(invokeTarget, "(", ")");
        if (org.apache.commons.lang3.StringUtils.isEmpty(methodStr)) {
            return Collections.emptyList();
        }
        String[] methodParams = methodStr.split(",");
        List<Object[]> classs = new LinkedList<>();
        for (int i = 0; i < methodParams.length; i++) {
            String str = org.apache.commons.lang3.StringUtils.trimToEmpty(methodParams[i]);
            
            // 空字符串处理
            if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
                classs.add(new Object[]{null, String.class});
                continue;
            }
            
            // String字符串类型，包含'或"
            if (org.apache.commons.lang3.StringUtils.contains(str, "'") || org.apache.commons.lang3.StringUtils.contains(str, "\"")) {
                // 移除引号
                String cleanStr = str.replaceAll("['\"]", "");
                classs.add(new Object[]{cleanStr, String.class});
            }
            // boolean布尔类型，等于true或者false
            else if (org.apache.commons.lang3.StringUtils.equals(str, "true") || org.apache.commons.lang3.StringUtils.equalsIgnoreCase(str, "false")) {
                classs.add(new Object[]{Boolean.valueOf(str), Boolean.class});
            }
            // long长整形，包含L
            else if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(str, "L")) {
                try {
                    classs.add(new Object[]{Long.valueOf(org.apache.commons.lang3.StringUtils.replaceIgnoreCase(str, "L", "")), Long.class});
                } catch (NumberFormatException e) {
                    // 如果无法解析为数字，则作为字符串处理
                    classs.add(new Object[]{str, String.class});
                }
            }
            // double浮点类型，包含D
            else if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(str, "D")) {
                try {
                    classs.add(new Object[]{Double.valueOf(org.apache.commons.lang3.StringUtils.replaceIgnoreCase(str, "D", "")), Double.class});
                } catch (NumberFormatException e) {
                    // 如果无法解析为数字，则作为字符串处理
                    classs.add(new Object[]{str, String.class});
                }
            }
            // 尝试解析为整数
            else {
                try {
                    classs.add(new Object[]{Integer.valueOf(str), Integer.class});
                } catch (NumberFormatException e) {
                    // 如果无法解析为数字，则作为字符串处理
                    classs.add(new Object[]{str, String.class});
                }
            }
        }
        return classs;
    }

    /**
     * 获取参数类型
     *
     * @param methodParams 参数相关列表
     * @return 参数类型列表
     */
    public static Class<?>[] getMethodParamsType(List<Object[]> methodParams) {
        Class<?>[] classs = new Class<?>[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            classs[index] = (Class<?>) os[1];
            index++;
        }
        return classs;
    }

    /**
     * 获取参数值
     *
     * @param methodParams 参数相关列表
     * @return 参数值列表
     */
    public static Object[] getMethodParamsValue(List<Object[]> methodParams) {
        Object[] classs = new Object[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            classs[index] = os[0];
            index++;
        }
        return classs;
    }
    
    /**
     * 测试危险关键字检查（仅用于调试）
     *
     * @param invokeTarget 调用目标字符串
     * @return true表示包含危险关键字，false表示安全
     */
    public static boolean testDangerousKeywords(String invokeTarget) {
        return containsDangerousKeywords(invokeTarget);
    }
    
    /**
     * 验证任务安全性
     *
     * @param invokeTarget 调用目标字符串
     * @throws SecurityException 如果任务不安全
     */
    public static void validateTaskSecurity(String invokeTarget) throws SecurityException {
        if (invokeTarget == null || invokeTarget.trim().isEmpty()) {
            throw new SecurityException("调用目标不能为空");
        }
        
        // 检查危险关键字
        if (containsDangerousKeywords(invokeTarget)) {
            throw new SecurityException("检测到危险关键字，拒绝执行");
        }
        
        String beanName = getBeanName(invokeTarget);
        
        if (isValidClassName(beanName)) {
            // 类名调用，检查包名
            if (!isAllowedClass(beanName)) {
                throw new SecurityException("不允许执行类: " + beanName);
            }
        } else {
            // Spring Bean调用，需要检查Bean的类是否在允许的包中
            try {
                Object bean = SpringUtils.getBean(beanName);
                String className = bean.getClass().getName();
                if (!isAllowedClass(className)) {
                    throw new SecurityException("不允许执行Bean: " + beanName + "，其类 " + className + " 不在允许的包中");
                }
            } catch (Exception e) {
                // Bean不存在或其他异常，记录日志但不阻止执行（让运行时处理）
                log.warn("验证Bean安全性时出现异常: {} -> {}", beanName, e.getMessage());
            }
        }
    }
    
    /**
     * 检查是否包含危险关键字
     *
     * @param invokeTarget 调用目标字符串
     * @return true表示包含危险关键字，false表示安全
     */
    private static boolean containsDangerousKeywords(String invokeTarget) {
        if (invokeTarget == null) {
            return false;
        }
        
        // 定义危险关键字列表（精确匹配，避免误判）
        String[] dangerousKeywords = {
            "snakeyaml", "Yaml.load", "ScriptEngineManager", "URLClassLoader", 
            "java.net.URL", "Runtime.getRuntime", "ProcessBuilder", 
            "ClassLoader", "defineClass", "getClass", "forName", "newInstance",
            "invoke", "getMethod", "getDeclaredMethod", "setAccessible",
            "http://", "https://", "ftp://", "file://", "jar://",
            "com.sun.", "sun.", "java.lang.reflect", "javax.script"
        };
        
        String lowerInvokeTarget = invokeTarget.toLowerCase();
        
        // 检查普通危险关键字
        for (String keyword : dangerousKeywords) {
            if (lowerInvokeTarget.contains(keyword.toLowerCase())) {
                log.warn("检测到危险关键字: {} 在调用目标中: {}", keyword, invokeTarget);
                return true;
            }
        }
        
        // 检查exec相关危险关键字，使用正则表达式避免误判execute方法
        // 匹配exec后面跟着非字母数字字符的情况，如exec(、exec[、exec.等
        // 但不匹配execute(、execution(等正常的方法名
        if (lowerInvokeTarget.matches(".*\\bexec[^a-zA-Z0-9_].*")) {
            // 进一步检查，排除execute、execution等正常方法名
            if (!lowerInvokeTarget.contains("execute") && !lowerInvokeTarget.contains("execution")) {
                log.warn("检测到危险关键字exec: {}", invokeTarget);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 安全地调用方法
     *
     * @param className 类名
     * @return 实例对象
     * @throws Exception 异常
     */
    private static Object invokeMethodSafely(String className) throws Exception {
        try {
            // 使用Class.forName进行安全检查
            Class<?> clazz = Class.forName(className);
            
            // 检查类是否在允许的包中
            if (!isAllowedClass(className)) {
                throw new SecurityException("不允许实例化类: " + className);
            }
            
            // 尝试获取无参构造函数
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                // 如果没有无参构造函数，尝试获取默认构造函数
                return clazz.newInstance();
            }
            
        } catch (ClassNotFoundException e) {
            throw new SecurityException("类不存在: " + className, e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SecurityException("无法实例化类: " + className, e);
        } catch (Exception e) {
            throw new SecurityException("实例化类时发生异常: " + className, e);
        }
    }
}
