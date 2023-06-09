package com.easyink.wecom.domain.vo.redeemcode;


import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCodeActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 兑换码活动VO
 * 类名： WeRedeemCodeActivityVO
 *
 * @author wx
 * @date 2022/7/4 11:00
 */
@Data
@ApiModel("兑换码活动VO")
public class WeRedeemCodeActivityVO extends WeRedeemCodeActivity {

    @ApiModelProperty("库存剩余")
    private Integer remainInventory;

    @ApiModelProperty("库存总数")
    private Integer sumInventory;

    @ApiModelProperty("告警员工及部门")
    private List<RedeemCodeAlarmUserVO> alarmUserList;

}
