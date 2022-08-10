package com.easyink.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名： OrderListVO
 *
 * @author 佚名
 * @date 2021/12/13 20:13
 */
@Data
@ApiModel("工单列表vo OrderListVO")
public class OrderListVO {
    @ApiModelProperty("工单id")
    private String orderId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("反馈单号")
    private String orderNum;
    @ApiModelProperty("反馈类型;有效值: 5775: 催件; 5776: 签收未收到; 5774: 投诉; 5777 : 拦截; 5782: 查件; 5778: 退回; 5779: 理赔; 5781: 破损; 5806: 改地址; 5807: 改电话号码; 5808: 遗失; 5809: 短少; 5810: 其他; 5811: 未收到取件码; 5812: 签收未收到改地址; 5813: 未送货上门; 5814: 提供底单; 5815: 核实重量; 5816: 无面单; 5817: 核实快递当前情况; 5818: 核实是否揽收; 5819: 核实为什么退回; 5820: 空包件，时效件; 5821: 提供红章")
    private Integer orderType;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
