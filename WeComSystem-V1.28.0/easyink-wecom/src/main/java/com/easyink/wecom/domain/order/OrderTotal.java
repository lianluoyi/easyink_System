package com.easyink.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: OrderTotal
 *
 * @author: 1*+
 * @date: 2021-12-13 18:21
 */
@Data
@ApiModel("工单数统计")
@NoArgsConstructor
public class OrderTotal implements Serializable {


    @ApiModelProperty("工单数")
    private String total;
    @ApiModelProperty("未处理数")
    private String notHandle;
    @ApiModelProperty("处理中数")
    private String inHandle;
    @ApiModelProperty("已处理数")
    private String finish;


}
