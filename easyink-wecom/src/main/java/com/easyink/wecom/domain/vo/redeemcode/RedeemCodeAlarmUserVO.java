package com.easyink.wecom.domain.vo.redeemcode;

import com.easyink.wecom.domain.entity.redeemcode.RedeemCodeAlarmUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * ClassName： RedeemCodeAlarmUserVO
 *
 * @author wx
 * @date 2022/7/7 15:43
 */
@Data
@ApiModel("兑换码告警员工VO")
public class RedeemCodeAlarmUserVO extends RedeemCodeAlarmUser {

    @ApiModelProperty(value = "员工，部门名称")
    private String businessName;

}
