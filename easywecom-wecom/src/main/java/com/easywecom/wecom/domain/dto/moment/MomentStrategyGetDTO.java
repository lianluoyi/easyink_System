package com.easywecom.wecom.domain.dto.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名： 获取规则组详情DTO
 *
 * @author 佚名
 * @date 2022/1/7 9:47
 */
@Data
@ApiModel("获取规则组详情DTO")
public class MomentStrategyGetDTO {
    @ApiModelProperty(value = "规则组id", required = true)
    @NotNull(message = "规则组id不能为空")
    private Integer strategy_id;
}
