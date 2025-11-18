package com.easyink.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类名: 线程池配置参数
 *
 * @author : silver_chariot
 * @date : 2023/7/26 10:51
 **/
@Component
@Data
@ConfigurationProperties(prefix = "thread-pool-prop")
public class ThreadPoolProperties {
    /**
     * 处理回调 线程池参数
     */
    private BaseThreadProperty handleCallback;
    /**
     * 表单任务 线程池参数
     */
    private BaseThreadProperty formTask;
    /**
     * 批量打标签 线程池参数
     */
    private BaseThreadProperty batchTag;
    /**
     * 同步编辑客户 线程池参数
     */
    private BaseThreadProperty syncCustomer;
    /**
     * 转发回调 线程池参数
     */
    private BaseThreadProperty sendCallback;
    /**
     * 获取员工执行群发结果 线程池参数(因为企微官方接口频率限制，所以这个线程池的最大线程数限制为5）
     */
    private BaseThreadProperty messageResultTask;
    /**
     * 新增员工与客户群发关系表线程池
     */
    private BaseThreadProperty momentRefTask;

    /**
     * 发送欢迎语线程池
     */
    private BaseThreadProperty welcomeMsg;

    /**
     * 基础线程池 参数
     */
    @Data
    public static class BaseThreadProperty {
        /**
         * 核心线程池数
         */
        private Integer corePoolSize =20 ;
        /**
         * 最大线程池数
         */
        private Integer maxPoolSize = 200 ;
        /**
         * 队列容量
         */
        private Integer queueCapacity = 1000;

        /**
         * 线程存活时间
         */
        private Integer keepAliveSeconds = 300;
    }
}
