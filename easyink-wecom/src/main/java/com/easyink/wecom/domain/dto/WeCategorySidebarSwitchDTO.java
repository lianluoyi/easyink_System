package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名：WeCategorySidebarSwitchDTO
 *
 * @author Society my sister Li
 * @date 2021-10-12 17:43
 */
@Data
@ApiModel("素材侧边栏开关实体")
public class WeCategorySidebarSwitchDTO {

    @ApiModelProperty(value = "类型ID", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "是否启用到侧边栏(0否，1是)", required = true)
    @NotNull(message = "using不能为空")
    private Boolean using;

    @ApiModelProperty(hidden = true)
    private String corpId;
}
