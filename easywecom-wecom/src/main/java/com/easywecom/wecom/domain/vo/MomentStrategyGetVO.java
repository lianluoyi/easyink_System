package com.easywecom.wecom.domain.vo;

import com.easywecom.wecom.domain.entity.moment.MomentStrategy;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 类名： MomentStrategyGetVO
 *
 * @author 佚名
 * @date 2022/1/7 9:55
 */
@ApiModel("获取规则组详情VO")
public class MomentStrategyGetVO {
    @ApiModelProperty("规则组")
    private MomentStrategy strategy;
}
