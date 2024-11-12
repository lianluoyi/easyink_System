package com.easyink.common.core.page;

/**
 * 分页执行函数
 *
 * @author tigger
 * 2023/4/3 17:29
 **/
public interface PageInvoke {

    /**
     * 分页方法
     */
    void page();

    /**
     * 清除分页
     */
    void clear();
}