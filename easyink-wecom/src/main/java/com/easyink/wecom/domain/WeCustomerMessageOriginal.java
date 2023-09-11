package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 群发消息 原始数据信息表 we_customer_messageOriginal
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_customer_messageOriginal")
public class WeCustomerMessageOriginal extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId
    @TableField("message_original_Id")
    private Long messageOriginalId;

    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "群发类型 0 发给客户 1 发给客户群")
    @TableField("push_type")
    private String pushType;
    @ApiModelProperty("客户添加开始时间")
    @TableField("customer_start_time")
    private Date customerStartTime;
    @ApiModelProperty("客户添加结束时间")
    @TableField("customer_end_time")
    private Date customerEndTime;

    @ApiModelProperty(value = "0 文本消息  1 图片消息 2 链接消息   3 小程序消息")
    @TableField("message_type")
    private String messageType;

    @ApiModelProperty("任务名称")
    @TableField("task_name")
    private String taskName;

    @ApiModelProperty(value = "员工id")
    @TableField("staff_id")
    private String staffId;

    @ApiModelProperty(value = "客户标签id列表")
    @TableField("tag")
    private String tag;

    @ApiModelProperty(value = "客户过滤标签id，多个用逗号隔开")
    @TableField("filter_tags")
    private String filterTags;

    @ApiModelProperty(value = "过滤员工id，多个用逗号隔开")
    @TableField("filter_users")
    private String filterUsers;

    @ApiModelProperty(value = "过滤部门id，多个用逗号隔开")
    @TableField("filter_departments")
    private String filterDepartments;

    @ApiModelProperty(value = "群组名称id")
    @TableField(exist = false)
    private String group;

    @ApiModelProperty(value = "部门id")
    @TableField("department")
    private String department;

    @ApiModelProperty(value = "消息范围 0 全部客户  1 指定客户")
    @TableField("push_range")
    private String pushRange;


    @ApiModelProperty(value = "是否删除 0代表存在 1代表删除")
    @TableField("del_flag")
    private Integer delFlag;

}
