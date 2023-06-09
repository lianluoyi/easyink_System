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
@ApiModel("工单更新返回")
public class OrderHandlerVO {
    @ApiModelProperty("工单id(工单系统内唯一)")
    private String orderId;
    @ApiModelProperty("微信消息id")
    private String messageId;
    @ApiModelProperty("文本消息")
    private String Message;
    @ApiModelProperty("变更后工单状态")
    private String orderStatus;
}
