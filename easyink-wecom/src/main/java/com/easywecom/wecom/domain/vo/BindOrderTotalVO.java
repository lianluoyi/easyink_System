package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： BindOrderTotalVO
 *
 * @author 佚名
 * @date 2021/12/14 16:29
 */
@Data
@ApiModel("工单账号绑定数量VO")
public class BindOrderTotalVO {

    @ApiModelProperty(value = "员工数量")
    private Integer userNum;
    @ApiModelProperty(value = "绑定数量")
    private Integer bindNum;
}
