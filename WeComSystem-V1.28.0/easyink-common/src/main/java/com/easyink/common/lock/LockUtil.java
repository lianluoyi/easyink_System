package com.easyink.common.lock;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * redis分布式锁工具类
 * @author jie.zhao
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LockUtil {

    private static RedissonClient redissonClient;

    /**
     * 设置工具类使用的redissonClient
     *
     * @param redissonClient
     */
    public static void setLocker(RedissonClient redissonClient) {
        LockUtil.redissonClient = redissonClient;
    }

    /**
     * 获取锁
     *
     * @param lockKey
     */
    public static RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

}