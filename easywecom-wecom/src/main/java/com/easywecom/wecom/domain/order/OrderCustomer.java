package com.easywecom.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: OrderCustomer
 *
 * @author: 1*+
 * @date: 2021-12-13 17:58
 */
@Data
@NoArgsConstructor
@ApiModel("工单系统客户")
public class OrderCustomer implements Serializable {

    @ApiModelProperty("工单系统客户ID")
    private String customerId;
    @ApiModelProperty("工单系统客户名")
    private String customerName;

}
