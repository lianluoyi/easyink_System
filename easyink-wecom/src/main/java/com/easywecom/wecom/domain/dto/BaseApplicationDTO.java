package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名: BaseApplicationDTO
 *
 * @author: 1*+
 * @date: 2021-10-18 9:32
 */
@Data
@ApiModel("应用中心基础查询实体")
public class BaseApplicationDTO {

    @ApiModelProperty("应用ID")
    @NotNull(message = "应用ID不能为空")
    private Integer appid;
}
