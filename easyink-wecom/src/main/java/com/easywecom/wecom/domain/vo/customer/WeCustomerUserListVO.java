package com.easywecom.wecom.domain.vo.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户所属员工列表VO
 *
 * @author tigger
 * 2022/3/24 15:12
 **/
@Data
public class WeCustomerUserListVO {

    @ApiModelProperty("员工id")
    private String userId;

    @ApiModelProperty("员工姓名")
    private String userName;

}
