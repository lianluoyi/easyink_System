package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author admin
 */
@Data
@ApiModel("素材收藏DTO")
public class WeChatCollectionDTO {

    @ApiModelProperty(value = "素材Id", required = true)
    private Long materialId;
    @ApiModelProperty(value = "员工ID", required = true)
    private String userId;

}
