package com.easyink.common.core.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author admin
 */
@ApiModel("表格分页数据对象")
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("总记录数")
    private int total;

    @ApiModelProperty("列表数据")
    private List<T> rows;

    @ApiModelProperty("消息状态码")
    private int code;

    @ApiModelProperty("消息内容")
    private String msg;

    /**
     * 表格数据对象
     */
    public TableDataInfo() {
    }

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
