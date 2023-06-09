package com.easyink.wecom.domain.dto;

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
@ApiModel("修改我的应用实体")
public class UpdateApplicationDTO extends BaseApplicationDTO {

    @ApiModelProperty("应用配置")
    @NotNull(message = "应用配置不能为空")
    private String config;
}
