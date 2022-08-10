package com.easywecom.wecom.domain.dto;

import com.easywecom.common.constant.WeConstans;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: 企业微信接口返回通用参数
 *
 * @author: 1*+
 * @date: 2021-08-04 14:05
 */
@Data
public class WeResultDTO {
    private Long id;
    private Integer errcode;
    private String errmsg;

    @ApiModelProperty("分页游标，下次请求时填写以获取之后分页的记录，如果已经没有更多的数据则返回空")
    private String next_cursor;

    public boolean isSuccess() {
        return WeConstans.WE_SUCCESS_CODE.equals(errcode);
    }

    public boolean isFail() {
        return !WeConstans.WE_SUCCESS_CODE.equals(errcode);
    }
}
