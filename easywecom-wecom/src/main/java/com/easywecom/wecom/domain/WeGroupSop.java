package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 群SOP规则
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@TableName("we_group_sop")
@EqualsAndHashCode(callSuper = true)
public class WeGroupSop extends BaseEntity {

    @ApiModelProperty(value = "群SOP主键")
    @TableId
    @TableField("rule_id")
    private Long ruleId;

    @ApiModelProperty(value = "规则名称")
    @TableField("rule_name")
    private String ruleName;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "开始执行时间")
    @TableField("start_time")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private String endTime;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "删除标志(0否,1是)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "1")
    private String delFlag;
}
