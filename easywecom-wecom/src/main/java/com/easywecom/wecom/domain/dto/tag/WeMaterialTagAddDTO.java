package com.easywecom.wecom.domain.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： 素材标签保存DTO
 *
 * @author 佚名
 * @date 2021/10/15 14:26
 */
@Data
@ApiModel("素材标签保存DTO 《WeMaterialTagAddDTO》")
public class WeMaterialTagAddDTO {
    @ApiModelProperty("标签名称")
    @NotBlank(message = "标签名称不能为空")
    private String tagName;
}
