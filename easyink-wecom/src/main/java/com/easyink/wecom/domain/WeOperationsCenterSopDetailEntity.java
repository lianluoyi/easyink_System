package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 类名：
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Data
@TableName("we_operations_center_sop_detail")
@ApiModel("实体")
@NoArgsConstructor
@AllArgsConstructor
public class WeOperationsCenterSopDetailEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;
    /**
     * sop的主键id
     */
    @ApiModelProperty(value = "sop的主键id")
    @TableField("sop_id")
    private Long sopId;
    /**
     * 规则id
     */
    @ApiModelProperty(value = "规则id")
    @TableField("rule_id")
    private Long ruleId;
    /**
     * 操作人/群主
     */
    @ApiModelProperty(value = "操作人/群主")
    @TableField("user_id")
    private String userId;
    /**
     * 消息接收者(当为客户时，填写客户userId；当为群时，填写群chatId)
     */
    @ApiModelProperty(value = "消息接收者(当为客户时，填写客户userId；当为群时，填写群chatId)")
    @TableField("target_id")
    private String targetId;
    /**
     * 是否已执行 0：未执行，1：已执行
     */
    @ApiModelProperty(value = "是否已执行 0：未执行，1：已执行")
    @TableField("is_finish")
    private Integer isFinish;
    /**
     * 提醒时间
     */
    @ApiModelProperty(value = "提醒时间")
    @TableField("alert_time")
    private Date alertTime;
    /**
     * 完成时间
     */
    @ApiModelProperty(value = "完成时间")
    @TableField("finish_time")
    private Date finishTime;


    public WeOperationsCenterSopDetailEntity(String corpId, Long sopId, Long ruleId, String userId, String targetId, Date alertTime) {
        this.corpId = corpId;
        this.sopId = sopId;
        this.ruleId = ruleId;
        this.userId = userId;
        this.targetId = targetId;
        this.isFinish = 0;
        this.alertTime = alertTime;
        this.finishTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, WeConstans.DEFAULT_SOP_END_TIME);
    }
}
