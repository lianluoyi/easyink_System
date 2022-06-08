package com.easywecom.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 类名: Order
 *
 * @author: 1*+
 * @date: 2021-12-13 18:13
 */
@Data
@NoArgsConstructor
@ApiModel("工单系统工单")
public class Order implements Serializable {

    @ApiModelProperty("工单id")
    private String orderId;
    @ApiModelProperty("反馈单号")
    private String orderNum;
    @ApiModelProperty("客户名称")
    private String customerName;
    @ApiModelProperty("反馈类型（有效值:0:催件;1:签收未收到;2:投诉;3:拦截;4:查件;5:退回;6:理赔;7:破损;8:改地址;9:改电话号码;10:遗失;11:短少;12:其他）")
    private String orderType;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("紧急程度（有效值:0:可延后;1:一般;2紧急）")
    private Integer urgencyLevel;
    @ApiModelProperty("反馈内容")
    private String content;
    @ApiModelProperty("工单状态(未处理,处理中,已处理)")
    private String orderStatus;
    @ApiModelProperty("反馈次数")
    private Integer feedback;
    @ApiModelProperty("微信消息ID")
    private String messageId;
    @ApiModelProperty("工单记录")
    private List<OrderLog> orderLog;


    @Data
    @ApiModel("订单日志")
    @NoArgsConstructor
    public class OrderLog implements Serializable {
        @ApiModelProperty("日志记录时间")
        private Date time;
        @ApiModelProperty("日志记录内容")
        private String content;
    }
}
