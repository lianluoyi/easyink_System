package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.constant.WeConstans;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * 类名： sop规则表
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Data
@TableName("we_operations_center_sop_rules")
@ApiModel("sop规则表实体")
public class WeOperationsCenterSopRulesEntity {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;
    /**
     * sop的主键id
     */
    @ApiModelProperty(value = "sop的主键id")
    @TableField("sop_id")
    private Long sopId;
    /**
     * 规则名称
     */
    @ApiModelProperty(value = "规则名称")
    @TableField("name")
    @Size(max = 32, message = "规则名称不能超过32字符")
    @NotBlank(message = "name不能为空")
    private String name;
    /**
     * 提醒类型
     * 0：xx小时xx分钟提醒，1：xx天xx:xx提醒，2：每天xx:xx提醒，3：每周周x的xx:xx提醒，4：每月x日xx:xx提醒
     */
    @ApiModelProperty(value = "提醒类型 0：xx小时xx分钟提醒，1：xx天xx:xx提醒，2：每天xx:xx提醒，3：每周周x的xx:xx提醒，4：每月x日xx:xx提醒")
    @TableField("alert_type")
    @NotNull(message = "alertType不能为空")
    private Integer alertType;
    /**
     * 提醒时间内容1
     */
    @ApiModelProperty(value = "提醒时间内容1")
    @TableField("alert_data1")
    @Size(max = 999, message = "alertData1不能超过范围0-999")
    private Integer alertData1 = WeConstans.DEFAULT_SOP_ALTER_DATA1;
    /**
     * 提醒时间内容2
     */
    @ApiModelProperty(value = "提醒时间内容2")
    @TableField("alert_data2")
    @NotBlank(message = "alertData2不能为空")
    private String alertData2;
}
