package com.easywecom.common.lock;
import java.util.concurrent.TimeUnit;

/**
 * 锁接口
 * @author jie.zhao
 */
public interface Locker {

    /**
     * 获取锁，如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。
     *
     * @param lockKey
     */
    void lock(String lockKey);

    /**
     * 释放锁
     *
     * @param lockKey
     */
    void unlock(String lockKey);

    /**
     * 获取锁,如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。如果获取到锁后，执行结束后解锁或达到超时时间后会自动释放锁
     *
     * @param lockKey
     * @param timeout
     */
    void lock(String lockKey, int timeout);

    /**
     * 获取锁,如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。如果获取到锁后，执行结束后解锁或达到超时时间后会自动释放锁
     *
     * @param lockKey
     * @param unit
     * @param timeout
     */
    void lock(String lockKey, TimeUnit unit, int timeout);

    /**
     * 尝试获取锁，获取到立即返回true,未获取到立即返回false
     *
     * @param lockKey
     * @return
     */
    boolean tryLock(String lockKey);

    /**
     * 尝试获取锁，在等待时间内获取到锁则返回true,否则返回false,如果获取到锁，则要么执行完后程序释放锁，
     * 要么在给定的超时时间leaseTime后释放锁
     *
     * @param lockKey
     * @param waitTime
     * @param leaseTime
     * @param unit
     * @return
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit)
            throws InterruptedException;

    /**
     * 锁是否被任意一个线程锁持有
     *
     * @param lockKey
     * @return
     */
    boolean isLocked(String lockKey);

}