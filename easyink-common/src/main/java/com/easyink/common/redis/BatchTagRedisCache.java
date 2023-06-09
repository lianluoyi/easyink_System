package com.easyink.common.redis;

import com.easyink.common.core.redis.RedisCache;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 类名: 批量打标签redis工具类
 *
 * @author : silver_chariot
 * @date : 2023/6/7 10:50
 **/
@Component("batchTagRedisCache")
public class BatchTagRedisCache extends RedisCache {
    /**
     * 中断任务key 前缀
     */
    private static final String STOP_TASK_KEY_PREFIX = "batchTag:stopTask:";

    /**
     * 获取中断任务key
     *
     * @param taskId 任务id
     * @return key
     */
    private String getStopTaskKey(Long taskId) {
        return STOP_TASK_KEY_PREFIX + taskId;
    }

    /**
     * 批量停止打标签任务
     *
     * @param taskIds 打标签任务id数组
     */
    public void batchStopTask(Long[] taskIds) {
        if (taskIds == null || ArrayUtils.isEmpty(taskIds)) {
            return;
        }
        redisTemplate.execute((RedisCallback<Integer>) conn -> {
            Arrays.stream(taskIds).forEach(this::stopTask);
            return null;
        });
    }

    /**
     * 中断批量打标签任务
     * <p>
     * 删除任务时调用
     *
     * @param taskId 任务id
     */
    public void stopTask(Long taskId) {
        if (taskId == null) {
            return;
        }
        // 增加任务 中断标识
        setCacheObject(getStopTaskKey(taskId), taskId, 24, TimeUnit.HOURS);
    }

    /**
     * 判断任务是否中断
     *
     * @param taskId 任务id
     * @return true 中断 false 没中断
     */
    public boolean isStopped(Long taskId) {
        if (taskId == null) {
            return false;
        }
        Long res = getCacheObject(getStopTaskKey(taskId));
        return res != null;
    }

    /**
     * 完成终端任务
     *
     * @param taskId 任务id
     */
    public void finishStop(Long taskId) {
        if (taskId == null) {
            return;
        }
        deleteObject(getStopTaskKey(taskId));
    }


}
