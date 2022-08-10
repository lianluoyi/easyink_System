package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名： 批量打标签DTO
 *
 * @author 佚名
 * @date 2021/10/12 13:58
 */

@ApiModel("批量打标签DTO《WeMaterialTagDTO》")
@Data
public class WeMaterialTagDTO {
    @ApiModelProperty("素材id列表")
    @NotNull
    private List<Long> materialIds;
    @ApiModelProperty("素材标签id列表")
    @NotNull
    private List<Long> tagIds;
}
