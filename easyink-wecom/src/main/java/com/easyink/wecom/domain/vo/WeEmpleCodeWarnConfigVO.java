package com.easyink.wecom.domain.vo;

import com.easyink.common.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 获客链接告警设置VO
 *
 * @author lichaoyu
 * @date 2023/8/23 16:40
 */
@Data
@NoArgsConstructor
public class WeEmpleCodeWarnConfigVO {

    @ApiModelProperty("企业id")
    private String corpId;
    @ApiModelProperty("链接不可用通知开关 True：开启，False：关闭")
    private Boolean linkUnavailableSwitch;
    @ApiModelProperty("链接不可用通知员工，多个用逗号隔开")
    private String linkUnavailableUsers;
    @ApiModelProperty(value = "链接不可用通知员工信息")
    private List<UserVO> linkUnavailableUsersInfo;
    @ApiModelProperty("链接不可用是否通知链接创建人，True：通知创建人，False：不通知创建人")
    private Boolean alarmCreater;
    @ApiModelProperty("链接不可用是否通知其他员工，True：通知其他员工，False：不通知其他员工")
    private Boolean alarmOtherUser;
    @ApiModelProperty("额度即将耗尽通知开关 True：开启，False：关闭")
    private Boolean balanceLowSwitch;
    @ApiModelProperty("额度即将耗尽通知员工，多个用逗号隔开")
    private String balanceLowUsers;
    @ApiModelProperty(value = "额度即将耗尽通知员工信息")
    private List<UserVO> balanceLowUsersInfo;
    @ApiModelProperty("额度已耗尽通知开关 True：开启，False：关闭")
    private Boolean balanceExhaustedSwitch;
    @ApiModelProperty("额度已耗尽通知员工，多个用逗号隔开")
    private String balanceExhaustedUsers;
    @ApiModelProperty(value = "额度已耗尽通知员工信息")
    private List<UserVO> balanceExhaustedUsersInfo;
    @ApiModelProperty("获客额度即将过期通知开关 True:开启，False：关闭")
    private Boolean quotaExpireSoonSwitch;
    @ApiModelProperty("获客额度即将过期通知员工，多个用逗号隔开")
    private String quotaExpireSoonUsers;
    @ApiModelProperty(value = "获客额度即将过期通知员工信息")
    private List<UserVO> quotaExpireSoonUsersInfo;
    @ApiModelProperty("告警类型 1：每次都告警，0：每天只告警一次")
    private Integer type;

    /**
     * 用于初始未设置企业告警信息的默认返回构造
     *
     * @param corpId 企业ID
     */
    public WeEmpleCodeWarnConfigVO(String corpId) {
        this.corpId = corpId;
        this.linkUnavailableSwitch = false;
        this.linkUnavailableUsers = StringUtils.EMPTY;
        this.linkUnavailableUsersInfo = Collections.emptyList();
        this.alarmCreater = false;
        this.alarmOtherUser = false;
        this.balanceLowSwitch = false;
        this.balanceLowUsers = StringUtils.EMPTY;
        this.balanceLowUsersInfo = Collections.emptyList();
        this.balanceExhaustedSwitch = false;
        this.balanceExhaustedUsers = StringUtils.EMPTY;
        this.balanceExhaustedUsersInfo = Collections.emptyList();
        this.quotaExpireSoonSwitch = false;
        this.quotaExpireSoonUsers = StringUtils.EMPTY;
        this.quotaExpireSoonUsersInfo = Collections.emptyList();
        this.type = 1;
    }
}
