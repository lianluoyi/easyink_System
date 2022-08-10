package com.easyink.wecom.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_pres_tag_group")
public class WePresTagGroupTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "老客户标签建群任务id")
    @TableId
    @TableField("task_id")
    private Long taskId = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "任务名称")
    @TableField("task_name")
    private String taskName;

    @ApiModelProperty(value = "发送方式 0: 企业群发 1：个人群发")
    @TableField("send_type")
    private Integer sendType;

    @ApiModelProperty(value = "群活码id")
    @TableField("group_code_id")
    private Long groupCodeId;

    @ApiModelProperty(value = "发送范围 0: 全部客户 1：部分客户")
    @TableField("send_scope")
    private Integer sendScope;

    @ApiModelProperty(value = "发送性别 0: 全部 1： 男 2： 女 3：未知")
    @TableField("send_gender")
    private Integer sendGender;

    @ApiModelProperty(value = "目标客户被添加起始时间")
    @TableField("cus_begin_time")
    private String cusBeginTime;

    @ApiModelProperty(value = "目标客户被添加结束时间")
    @TableField("cus_end_time")
    private String cusEndTime;

    @ApiModelProperty(value = "加群引导语")
    @TableField("welcome_msg")
    private String welcomeMsg;

    @ApiModelProperty(value = "企业群发消息的id")
    @TableField("msgid")
    private String msgid;

    @ApiModelProperty(value = "是否删除")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}
