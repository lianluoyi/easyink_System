package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名： WeMaterialTagRelRemoveDTO
 *
 * @author 佚名
 * @date 2021/10/18 14:55
 */
@Data
@ApiModel("批量移除素材标签DTO《WeMaterialTagRelRemoveDTO》")
public class WeMaterialTagRelRemoveDTO {
    @ApiModelProperty("素材标签id列表")
    @NotEmpty(message = "素材标签id列表不能为空")
    private List<Long> tagIds;

    @ApiModelProperty("素材id列表")
    @NotEmpty(message = "素材id列表不能为空")
    private List<Long> materialIds;

}
