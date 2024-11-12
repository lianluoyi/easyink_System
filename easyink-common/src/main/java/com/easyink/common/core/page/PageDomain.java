package com.easyink.common.core.page;

import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.StringUtils;

/**
 * 分页数据
 *
 * @author admin
 */
public class PageDomain implements PageInvoke {
    /**
     * 当前记录起始索引
     */
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "asc";


    public PageDomain() {
        //lastId存在，则采用游标分页方式
        if (StringUtils.isNotBlank(ServletUtils.getParameter(TableSupport.LAST_ID))) {
            //默认从第一页开始
            this.pageNum = Integer.valueOf("1");
        } else {
            this.pageNum = ServletUtils.getParameterToInt(TableSupport.PAGE_NUM);
        }
        this.pageSize = ServletUtils.getParameterToInt(TableSupport.PAGE_SIZE);
        this.orderByColumn = ServletUtils.getParameter(TableSupport.ORDER_BY_COLUMN);
        this.isAsc = ServletUtils.getParameter(TableSupport.IS_ASC);
    }

    public String getOrderBy() {
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }

    @Override
    public void page() {

    }

    @Override
    public void clear() {

    }
}
