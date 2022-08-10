package com.easyink.common.utils.sql;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 类名: 分批批量插入工具
 *
 * @author : silver_chariot
 * @date : 2021/11/4 10:58
 */
public class BatchInsertUtil {

    private BatchInsertUtil() {
    }

    /**
     * 默认单次最大插入量为500
     */
    public static final int DEFAULT_MAX_SINGLE_BATCH_NUM = 500;

    /**
     * 分批批量插入集合
     *
     * @param list     集合
     * @param executor 执行方法 {@link IBatchInsertExecutor}
     * @param batchNum 单次最大插入量
     * @param <T>      元素类型
     */
    public static <T> void doInsert(List<T> list, IBatchInsertExecutor<T> executor, int batchNum) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        int listSize = list.size();
        // 起始索引
        int index = 0;
        // 大于最大单次插入量时需要分批插入
        while (listSize > batchNum) {
            executor.insert(list.subList(index, index + batchNum));
            index += batchNum;
            listSize -= batchNum;
        }
        if (listSize > 0) {
            executor.insert(list.subList(index, index + listSize));
        }
    }

    /**
     * 分批批量插入集合
     *
     * @param list     集合
     * @param executor 执行方法 {@link IBatchInsertExecutor}
     * @param <T>      元素类型
     */
    public static <T> void doInsert(List<T> list, IBatchInsertExecutor<T> executor) {
        doInsert(list, executor, DEFAULT_MAX_SINGLE_BATCH_NUM);
    }
}
