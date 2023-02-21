package com.easyink.wecom.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池管理类
 *
 * @author wx
 * 2023/1/14 16:00
 **/
@Component
@Slf4j
public class ThreadPoolManager {

    /**
     * 默认线程池配置(后续可改成配置文件读取)
     */
    protected static final int CORE_POOL_SIZE = 20;
    protected static final int MAX_POOL_SIZE = 40;
    protected static final int QUEUE_CAPACITY = 30;
    protected static final int KEEP_ALIVE_SECOND = 600;

    /**
     * 表单线程池执行器
     *
     * @return executor
     */
    @Bean("formTaskExecutor")
    @Primary
    public ThreadPoolTaskExecutor formTaskExecutor() {
        ThreadPoolTaskExecutor executor = init(CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY, KEEP_ALIVE_SECOND);
        return construct(executor, "formTask");
    }

    /**
     * 初始化线程池
     *
     * @param corePoolSize    核心线程数
     * @param maxPoolSize     最大线程数
     * @param queueCapacity   队列容量
     * @param keepAliveSecond 最大存活时间
     * @return {@link ThreadPoolTaskExecutor}
     */
    public ThreadPoolTaskExecutor init(int corePoolSize, int maxPoolSize, int queueCapacity, int keepAliveSecond) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //  完成任务自动关闭 , 默认为false
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //  核心线程超时退出，默认为false
        executor.setAllowCoreThreadTimeOut(true);
        //核心线程池大小
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列容量
        executor.setQueueCapacity(queueCapacity);
        //活跃时间
        executor.setKeepAliveSeconds(keepAliveSecond);
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
        // 使用默认拒绝策略
        // todo 排查直接传new ThreadPoolExecutor.AbortPolicy() 或者其他策略会报编译错误
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(namePrefix + "Thread-%d").setUncaughtExceptionHandler(
                        (Thread t, Throwable e) ->
                                log.error("[" + namePrefix + "-thread-pool]thread pool error occurs:{}", ExceptionUtils.getStackTrace(e)))
                .build();
        executor.setThreadFactory(threadFactory);
        executor.initialize();
        return executor;
    }

}
