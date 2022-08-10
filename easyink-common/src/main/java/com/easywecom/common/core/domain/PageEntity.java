package com.easywecom.common.core.domain;

import java.io.Serializable;

/**
 * 分页entity
 *
 * @author tigger
 * 2022/3/7 17:30
 **/
public class PageEntity implements Serializable {

    private Integer pageSize = 10;

    private Integer pageNum = 1;


    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }
}
