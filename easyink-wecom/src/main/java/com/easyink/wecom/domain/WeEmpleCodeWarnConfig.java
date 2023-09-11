package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获客链接告警设置表实体类
 *
 * @author lichaoyu
 * @date 2023/8/23 9:23
 */
@Data
@TableName("we_emple_code_warn_config")
public class WeEmpleCodeWarnConfig extends BaseEntity {

    @ApiModelProperty("企业id")
    @TableId
    @TableField("corp_id")
    private String corpId;
    @ApiModelProperty("链接不可用通知开关 True：开启，False：关闭")
    @TableField("link_unavailable_switch")
    private Boolean linkUnavailableSwitch;
    @ApiModelProperty("链接不可用通知员工，多个用逗号隔开")
    @TableField("link_unavailable_users")
    private String linkUnavailableUsers;
    @ApiModelProperty("链接不可用是否通知链接创建人，True：通知创建人，False：不通知创建人")
    @TableField("alarm_creater")
    private Boolean alarmCreater;
    @ApiModelProperty("链接不可用是否通知其他员工，True：通知其他员工，False：不通知其他员工")
    @TableField("alarm_other_user")
    private Boolean alarmOtherUser;
    @ApiModelProperty("额度即将耗尽通知开关 True：开启，False：关闭")
    @TableField("balance_low_switch")
    private Boolean balanceLowSwitch;
    @ApiModelProperty("额度即将耗尽通知员工，多个用逗号隔开")
    @TableField("balance_low_users")
    private String balanceLowUsers;
    @ApiModelProperty("额度已耗尽通知开关 True：开启，False：关闭")
    @TableField("balance_exhausted_switch")
    private Boolean balanceExhaustedSwitch;
    @ApiModelProperty("额度已耗尽通知员工，多个用逗号隔开")
    @TableField("balance_exhausted_users")
    private String balanceExhaustedUsers;
    @ApiModelProperty("获客额度即将过期通知开关 True:开启，False：关闭")
    @TableField("quota_expire_soon_switch")
    private Boolean quotaExpireSoonSwitch;
    @ApiModelProperty("获客额度即将过期通知员工，多个用逗号隔开")
    @TableField("quota_expire_soon_users")
    private String quotaExpireSoonUsers;
    @ApiModelProperty("告警类型 1：每次都告警，0：每天只告警一次")
    @TableField("type")
    private Integer type;

}
