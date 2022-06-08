package com.easywecom.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 类名: OrderNetworkVO
 *
 * @author: 1*+
 * @date: 2021-12-15 13:33
 */
@Data
@ApiModel("工单系统网点信息")
public class OrderNetworkVO implements Serializable {


    @ApiModelProperty("网点ID")
    private String networkId;
    @ApiModelProperty("网点名")
    private String networkName;
    @ApiModelProperty("快递公司标识")
    private String expressName;

    @ApiModelProperty(value = "企业名")
    private String companyName;

    @ApiModelProperty(value = "企业ID")
    private String corpId;

}
