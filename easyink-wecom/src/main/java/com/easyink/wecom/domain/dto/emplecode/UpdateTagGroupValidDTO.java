package com.easyink.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新员工活码标签组配置DTO
 *
 * @author System
 * @date 2025-09-16
 */
@Data
@ApiModel("更新员工活码标签组配置DTO")
public class UpdateTagGroupValidDTO {

    @ApiModelProperty(value = "员工活码ID", required = true)
    @NotNull(message = "员工活码ID不能为空")
    private Long empleCodeId;

    @ApiModelProperty(value = "企业ID", required = true)
    @NotNull(message = "企业ID不能为空")
    private String corpId;

    @ApiModelProperty(value = "标签组范围配置:0:任一标签组, 1:全部标签组", required = true)
    @NotNull(message = "标签组配置不能为空")
    private Integer tagGroupValid;
}
