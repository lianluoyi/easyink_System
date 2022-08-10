package com.easyink.wecom.domain.dto.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量启用禁用DTO
 *
 * @author tigger
 * 2022/3/1 15:31
 **/
@Data
public class TagRuleBatchStatusDTO {

    @ApiModelProperty("启用禁用状态 0:禁用1:启用")
    private Boolean status;
    @NotEmpty(message = "请至少选择一个修改的目标")
    @ApiModelProperty("规则id列表")
    private List<Long> idList;
    private String corpId;
}
