package com.easywecom.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： OrderBindInfoVO
 *
 * @author 佚名
 * @date 2021/12/15 15:24
 */
@Data
@NoArgsConstructor
@ApiModel("员工客户绑定信息vo")
public class OrderBindInfoVO {
    @ApiModelProperty("工单系统帐号ID 未绑定为空")
    private String orderUserId;

    @ApiModelProperty("工单系统帐号名 未绑定为空")
    private String orderUserName;

    @ApiModelProperty(value = "工单客户ID 未绑定为空")
    private String orderCustomerId;

    @ApiModelProperty(value = "工单客户名 未绑定为空")
    private String orderCustomerName;
}
