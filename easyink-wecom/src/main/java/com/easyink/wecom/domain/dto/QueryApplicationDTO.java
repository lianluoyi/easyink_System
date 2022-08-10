package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名: QueryApplicationDTO
 *
 * @author: 1*+
 * @date: 2021-10-15 17:21
 */
@Data
@ApiModel("应用中心查询条件实体")
public class QueryApplicationDTO {

    @ApiModelProperty("应用名")
    private String name;

    @ApiModelProperty(value = "应用类型", required = true)
    @NotNull(message = "应用类型不能为空值")
    private Integer type;

}
