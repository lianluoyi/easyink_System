package com.easyink.common.core.page;

/**
 * 表格数据处理
 *
 * @author admin
 */
public class TableSupport {
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 游标分页,lastId表示获得userId = lastId后面的员工数据
     */
    public static final String LAST_ID = "lastId";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    public static PageDomain buildPageRequest() {
        return new PageDomain();

    }
}
