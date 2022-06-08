package com.easywecom.wecom.domain.dto.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;

/**
 * 类名： 获取规则组列表DTO
 *
 * @author 佚名
 * @date 2022/1/7 9:36
 */
@Data
@ApiModel("获取规则组列表DTO")
public class MomentStrategyDTO {
    @ApiModelProperty(value = "用于分页查询的游标，字符串类型，由上一次调用返回，首次调用可不填")
    private String cursor;
    @ApiModelProperty(value = "返回的最大记录数，整型，最大值1000，默认值500，超过最大值时取默认值")
    @Max(1000)
    private Integer limit;
}
