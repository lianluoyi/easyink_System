package com.easywecom.wecom.domain.entity.redeemcode;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * ClassName： RedeemCodeAlarmUser
 *
 * @author wx
 * @date 2022/7/5 19:08
 */
@Data
@ApiModel("兑换码告警员工")
public class RedeemCodeAlarmUser {
    @ApiModelProperty(value = "兑换码活动id")
    @TableField("activity_id")
    private Long activityId;

    @ApiModelProperty(value = "targetId， type : 1时存储部门id，为2时存储员工id")
    @TableField("target_id")
    @NotNull(message = "员工id或部门id不能为空")
    private String targetId;

    @ApiModelProperty(value = "targetId， type : 1时表示部门，为2时表示员工")
    @TableField("type")
    @NotNull(message = "兑换码警告员工，type不能为空")
    private Integer type;

}
