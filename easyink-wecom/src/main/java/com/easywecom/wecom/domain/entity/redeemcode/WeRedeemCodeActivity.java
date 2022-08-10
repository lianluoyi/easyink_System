package com.easywecom.wecom.domain.entity.redeemcode;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.core.domain.BaseEntity;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 兑换码
 * 类名： WeRedeemCodeActivity
 *
 * @author wx
 * @date 2022/7/4 11:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_redeem_code_activity")
@ApiModel("兑换码活动")
public class WeRedeemCodeActivity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "兑换码活动id, 主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "兑换码活动名")
    @TableField("name")
    @NotBlank(message = "活动名称不能空")
    @Valid
    @Size(max = 32, message = "活动名称超过限制,最多32个字符")
    private String activityName;

    @ApiModelProperty(value = "活动开始时间")
    @TableField("start_time")
    private String effectStartTime;

    @ApiModelProperty(value = "活动结束时间")
    @TableField("end_time")
    private String effectEndTime;

    @ApiModelProperty(value = "创建人", hidden = true)
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新人", hidden = true)
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "参与限制")
    @TableField("enable_limited")
    private Integer enableLimited;

    @ApiModelProperty(value = "告警通知")
    @TableField("enable_alarm")
    private Integer enableAlarm;

    @ApiModelProperty(value = "告警阈值")
    @TableField("alarm_threshold")
    private Integer alarmThreshold;
}
