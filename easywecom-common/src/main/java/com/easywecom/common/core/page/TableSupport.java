package com.easywecom.common.core.page;

import com.easywecom.common.utils.ServletUtils;
import com.easywecom.common.utils.StringUtils;

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

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        //lastId存在，则采用游标分页方式
        if(StringUtils.isNotBlank(ServletUtils.getParameter(LAST_ID))){
            //默认从第一页开始
            pageDomain.setPageNum(Integer.valueOf("1"));
        }else{
            pageDomain.setPageNum(ServletUtils.getParameterToInt(PAGE_NUM));
        }
        pageDomain.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        return pageDomain;
    }

    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }
}
