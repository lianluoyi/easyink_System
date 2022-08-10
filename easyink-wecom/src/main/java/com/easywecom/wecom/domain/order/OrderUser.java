package com.easywecom.wecom.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: OrderUser
 *
 * @author: 1*+
 * @date: 2021-12-13 17:55
 */
@Data
@NoArgsConstructor
@ApiModel("工单系统用户")
public class OrderUser implements Serializable {

    @ApiModelProperty("工单系统帐号ID")
    @JsonProperty("orderUserId")
    private String userId;
    @ApiModelProperty("工单系统帐号名")
    @JsonProperty("orderUserName")
    private String userName;


}
