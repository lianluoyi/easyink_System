package com.easyink.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： OrderVerifyVO
 *
 * @author 佚名
 * @date 2021/12/13 20:50
 */
@Data
@NoArgsConstructor
@ApiModel("工单系统校验网点ID VO")
public class OrderVerifyVO {
    @ApiModelProperty("网点名称")
    private String networkName;

    @ApiModelProperty("快递公司名称 中通快递，圆通速递，韵达快递，极兔快递，申通快递，跨越速递")
    private String logistics;
}
