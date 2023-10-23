package com.easyink.framework.config;

import com.easyink.common.utils.Threads;
import com.easyink.framework.config.properties.ThreadPoolProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @author admin
 **/
@Configuration
@Slf4j
public class ThreadPoolConfig {
    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    // 核心线程池大小
    private int corePoolSize = 50;

    // 最大可创建的线程数
    private int maxPoolSize = 200;

    // 队列最大长度
    private int queueCapacity = 1000;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 300;

    @Bean(name = "threadPoolTaskExecutor")
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getHandleCallback();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), prop.getQueueCapacity(), prop.getKeepAliveSeconds(),"threadPool");
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }

    /**
     * 处理回调的线程池执行器
     * @return
     */
    @Bean("handleCallbackExecutor")
    public ThreadPoolTaskExecutor handleCallbackExecutor( ) {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getHandleCallback();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), prop.getQueueCapacity(), prop.getKeepAliveSeconds(),"formTask");
    }
    /**
     * 表单线程池执行器
     *
     * @return executor
     */
    @Bean("formTaskExecutor")
    public ThreadPoolTaskExecutor formTaskExecutor() {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getFormTask();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), prop.getQueueCapacity(), prop.getKeepAliveSeconds(),"formTask");
    }

    /**
     * 批量标签线程池
     *
     * @return executor
     */
    @Bean("batchTagExecutor")
    public ThreadPoolTaskExecutor batchTagExecutor() {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getFormTask();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), prop.getQueueCapacity(), prop.getKeepAliveSeconds(),"batchTag");
    }

    /**
     * 同步编辑客户回调 线程池
     *
     * @return
     */
    @Bean("syncEditCustomerExecutor")
    public ThreadPoolTaskExecutor syncEditCustomerExecutor() {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getFormTask();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), prop.getQueueCapacity(), prop.getKeepAliveSeconds(),"syncEditCustomer");
    }

    /**
     * 发送回调线程池
     *
     * @return
     */
    @Bean("sendCallbackExecutor")
    public ThreadPoolTaskExecutor sendCallbackExecutor() {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getFormTask();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), prop.getQueueCapacity(), prop.getKeepAliveSeconds(), "sendCallback");

    }

    /**
     * 获取员工执行群发结果线程池(因为企微官方接口频率限制，所以这个线程池的最大线程数限制为5）
     *
     * @return
     */
    @Bean("messageResultTaskExecutor")
    public ThreadPoolTaskExecutor messageResultTaskExecutor() {
        ThreadPoolProperties.BaseThreadProperty prop = threadPoolProperties.getMessageResultTask();
        return init(prop.getCorePoolSize(), prop.getMaxPoolSize(), null, prop.getKeepAliveSeconds(), "messageResultTask");
    }

    /**
     * 构建线程池
     *
     * @param corePoolSize             核心线程数
     * @param maxPoolSize              最大线程数
     * @param keepAliveSeconds         活跃时间
     * @param queueCapacity            队列容量
     * @param poolNamePrefix           线程池名前缀
     * @return {@link ThreadPoolTaskExecutor}
     */
    public ThreadPoolTaskExecutor init(Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity, Integer keepAliveSeconds, String poolNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //  完成任务自动关闭 , 默认为false
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //  核心线程超时退出，默认为false
        executor.setAllowCoreThreadTimeOut(true);
        //核心线程池大小
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        if (queueCapacity != null) {
            //队列容量
            executor.setQueueCapacity(queueCapacity);
        }
        //活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        construct(executor, poolNamePrefix);
        return executor;
    }

    /**
     * 构建线程池执行器
     *
     * @param executor   初始化后的执行器
     * @param namePrefix 线程名称前缀,如传getContact,线程名前缀则为 getContactThread-threadId
     * @return {@link ThreadPoolTaskExecutor}
     */
    public ThreadPoolTaskExecutor construct(ThreadPoolTaskExecutor executor, String namePrefix) {
        //线程名字前缀
        executor.setThreadNamePrefix(namePrefix + "Thread-%d");
        log.info("[" + namePrefix + "-thread-pool] init success ...");
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(namePrefix + "Thread-%d")
                                                                .setUncaughtExceptionHandler(
                                                                        (Thread t, Throwable e) ->
                                                                                log.error("[" + namePrefix + "-thread-pool]thread pool error occurs:{}", ExceptionUtils.getStackTrace(e)))
                                                                .build();
        executor.setThreadFactory(threadFactory);
        executor.initialize();
        return executor;
    }
}
