package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeEmplyCodeScopeUserVO
 *
 * @author Society my sister Li
 * @date 2021-12-20 11:05
 */
@Data
@ApiModel("员工活码的员工数据")
public class WeEmplyCodeScopeUserVO {

    @ApiModelProperty("员工userId")
    private String userId;

    @ApiModelProperty("员工姓名")
    private String userName;
}
