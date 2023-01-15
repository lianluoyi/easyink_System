package com.easyink.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.*;

/**
 * 线程相关工具类.
 *
 * @author admin
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Threads {

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("common-pool-%d").build();


    /**
     * 创建线程池
     */
    public static final ThreadPoolExecutor SINGLE_THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE + 1, 10L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), NAMED_THREAD_FACTORY, (r, executor) -> {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            log.error("InterruptedException e:{}", ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
        }
    });


    /**
     * sleep等待,单位为毫秒
     */
    public static void sleep(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    /**
     * 停止线程池
     * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
     * 如果超时, 则调用shutdownNow, 取消在workQueue中Pending的任务,并中断所有阻塞函数.
     * 如果仍人超時，則強制退出.
     * 另对在shutdown时线程本身被调用中断做了处理.
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        int timeOut = 120;
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(timeOut, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(timeOut, TimeUnit.SECONDS)) {
                        log.info("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 打印线程异常信息
     */
    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            log.error(t.getMessage(), t);
        }
    }

}
