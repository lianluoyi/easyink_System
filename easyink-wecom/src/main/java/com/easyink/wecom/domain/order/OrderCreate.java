package com.easyink.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： OrderCreate
 *
 * @author 佚名
 * @date 2021/12/13 20:06
 */
@Data
@ApiModel("工单创建返回")
public class OrderCreate {
    @ApiModelProperty("工单id(工单系统内唯一)")
    private String orderId;
    @ApiModelProperty("微信消息id")
    private String messageId;
    @ApiModelProperty("客户id")
    private String customerId;
    @ApiModelProperty("固定值:未处理(新建单默认状态)")
    private String orderStatus;
}
