package com.easyink.common.utils.sql;

import java.util.List;

/**
 * 类名: 分批批量插入执行器接口
 *
 * @author : silver_chariot
 * @date : 2021/11/4 11:01
 */
public interface IBatchInsertExecutor<T> {
    /**
     * 批量插入
     *
     * @param list 需要插入的集合
     */
    void insert(List<T> list);
}
