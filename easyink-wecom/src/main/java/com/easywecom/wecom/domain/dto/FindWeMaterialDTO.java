package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeMaterialDTO
 *
 * @author Society my sister Li
 * @date 2021-10-13 10:56
 */
@Data
@ApiModel("查询素材条件实体")
public class FindWeMaterialDTO {

    @ApiModelProperty(value = "素材类型")
    private Integer mediaType;

    @ApiModelProperty(value = "搜索标题")
    private String search;

    @ApiModelProperty(value = "是否发布")
    private Boolean showMaterial;

    @ApiModelProperty(value = "是否过期")
    private Boolean isExpire;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "标签ID,多个逗号隔开")
    private String tagIds;
}
