package com.easyink.wecom.domain.vo.customerloss;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户继承 流失提醒客户标签开关
 * 类名：CustomerChurnSwitchVO
 *
 * @author lichaoyu
 * @date 2023/3/23 14:37
 */
@Data
@ApiModel("查询客户标签和客户流失提醒开关状态")
public class CustomerLossSwitchVO {

    @ApiModelProperty("客户流失提醒开关")
    private String customerChurnNoticeSwitch;

    @ApiModelProperty("客户流失标签开关")
    private String customerLossTagSwitch;
}
