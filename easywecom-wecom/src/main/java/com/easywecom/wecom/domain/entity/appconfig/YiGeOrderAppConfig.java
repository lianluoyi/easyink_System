package com.easywecom.wecom.domain.entity.appconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 壹鸽快递工单助手配置
 *
 * @author 佚名
 * @date 2021/12/13 16:55
 */
@Data
@ApiModel("壹鸽快递工单助手配置 YiGeOrderAppConfig")
public class YiGeOrderAppConfig {
    @ApiModelProperty("快递类型 1圆通 2韵达 3中通")
    private Integer type;
    @ApiModelProperty("网点id")
    private String networkId;
    @ApiModelProperty("网点名称")
    private String networkName;
}
