package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名：RemoveMaterialDTO
 *
 * @author Society my sister Li
 * @date 2021-10-13 16:19
 */
@Data
@ApiModel("删除素材实体")
public class RemoveMaterialDTO {

    @ApiModelProperty(value = "需删除的素材列表",required = true)
    @NotNull(message = "ids不能为空")
    private String ids;

    @ApiModelProperty(value = "mediaType",required = true)
    @NotNull(message = "mediaTypet不能为空")
    private Integer mediaType;

    @ApiModelProperty(hidden = true)
    private String corpId;
}
