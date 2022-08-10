package com.easyink.wecom.domain.query.groupcode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 客户群活码详情Query
 *
 * @author tigger
 * 2022/2/11 10:03
 **/
@Data
public class GroupCodeDetailQuery {
    @ApiModelProperty("客户群活码id")
    @NotNull(message = "客户群活码id不能为空")
    private Long id;
    @ApiModelProperty("使用状态")
    private Integer status;
}
