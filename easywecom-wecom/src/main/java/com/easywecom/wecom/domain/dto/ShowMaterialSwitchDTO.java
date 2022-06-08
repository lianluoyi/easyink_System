package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 类名：ShowMaterialSwitchDTO
 *
 * @author Society my sister Li
 * @date 2021-10-12 18:24
 */
@Data
@ApiModel("素材发布/批量发布实体")
public class ShowMaterialSwitchDTO {


    @ApiModelProperty(value = "素材主键ID", required = true)
    @NotEmpty(message = "ids不能为空")
    private String ids;


    @ApiModelProperty(value = "是否发布到侧边栏（0否，1是）", required = true)
    @NotNull(message = "showMaterial不能为空")
    private Boolean showMaterial;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "素材类型", required = true)
    @NotNull(message = "mediaType不能为空")
    private Integer mediaType;

}
