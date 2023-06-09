package com.easyink.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： OrderListMainVO
 *
 * @author 佚名
 * @date 2021/12/15 14:29
 */
@Data
@ApiModel("工单列表主vo OrderListVO")
public class OrderListMainVO {

    @ApiModelProperty(value = "工单客户ID")
    private String orderCustomerId;

    @ApiModelProperty(value = "工单客户名")
    private String orderCustomerName;

    @ApiModelProperty("工单列表")
    private List<OrderListVO> orderList;
}
