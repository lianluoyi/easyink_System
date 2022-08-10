package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名：RestoreMaterialDTO
 *
 * @author Society my sister Li
 * @date 2021-10-13 15:15
 */
@Data
@ApiModel("过期素材恢复实体")
public class RestoreMaterialDTO {

    @ApiModelProperty(value = "ids", required = true)
    @NotNull(message = "ids不能为空")
    private Long[] ids;

    @ApiModelProperty(value = "素材类型", required = true)
    @NotNull(message = "mediaType不能为空")
    private Integer mediaType;

    @ApiModelProperty(hidden = true)
    private String corpId;
}
