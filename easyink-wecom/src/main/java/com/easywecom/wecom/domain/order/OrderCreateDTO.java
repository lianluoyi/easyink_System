package com.easywecom.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 类名： OrderCreateDTO
 *
 * @author 佚名
 * @date 2021/12/13 20:13
 */
@Data
@ApiModel("工单创建DTO")
public class OrderCreateDTO {
    @ApiModelProperty(value = "网点id", required = true)
    @NotBlank
    private String networkId;

    @ApiModelProperty(value = "客服id", required = true)
    @NotBlank
    private String userId;


    @ApiModelProperty(value = "微信消息id", required = true)
    @NotBlank
    private String messageId;

    @ApiModelProperty(value = "客户id", required = true)
    @NotBlank
    private String customerId;
    @ApiModelProperty(value = "快递单号", required = true)
    @NotBlank
    private String orderNum;

    @ApiModelProperty("联系人")
    private String contacts;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty(" 紧急程度;有效值:0:可延后;1:一般;2紧急")
    private String urgencyLevel;

    @ApiModelProperty(" 工单内容")
    private String content;

    @ApiModelProperty(value = "有效值: 5775: 催件; 5776: 签收未收到; 5774: 投诉; 5777 : 拦截; 5782: 查件; 5778: 退回; 5779: 理赔; 5781: 破损; 5806: 改地址; 5807: 改电话号码; 5808: 遗失; 5809: 短少; 5810: 其他; 5811: 未收到取件码; 5812: 签收未收到改地址; 5813: 未送货上门; 5814: 提供底单; 5815: 核实重量; 5816: 无面单; 5817: 核实快递当前情况; 5818: 核实是否揽收; 5819: 核实为什么退回; 5820: 空包件，时效件; 5821: 提供红章", required = true)
    @NotNull
    private Integer type;


}
