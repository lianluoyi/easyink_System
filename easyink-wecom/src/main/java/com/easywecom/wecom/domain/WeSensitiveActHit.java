package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.annotation.Excel;
import com.easywecom.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author 佚名
 * @date 2021-7-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "we_sensitive_act_hit")
@ApiModel(value = "敏感行为记录")
public class WeSensitiveActHit extends BaseEntity {
    private static final long serialVersionUID = 7818042206556052997L;

    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 敏感行为操作人id
     */
    @TableField(value = "operator_id")
    @NotBlank(message = "操作人id不能为空")
    @ApiModelProperty(value = "操作人id")
    private String operatorId;

    /**
     * 敏感行为操作人
     */
    @TableField(value = "operator")
    @NotBlank(message = "操作人不能为空")
    @ApiModelProperty(value = "操作人")
    @Excel(name = "操作人", sort = 1)
    private String operator;


    /**
     * 敏感行为操作对象id
     */
    @TableField(value = "operate_target_id")
    @NotBlank(message = "操作对象id不能为空")
    @ApiModelProperty(value = "操作对象id")
    private String operateTargetId;

    /**
     * 敏感行为操作对象
     */
    @TableField(value = "operate_target")
    @NotBlank(message = "操作对象不能为空")
    @ApiModelProperty(value = "操作对象")
    @Excel(name = "操作对象", sort = 2)
    private String operateTarget;

    /**
     * 敏感行为id
     */
    @TableField(value = "sensitive_act_id")
    @NotNull(message = "敏感行为id不能为空")
    @ApiModelProperty(value = "敏感行为id")
    private Long sensitiveActId;

    /**
     * 敏感行为名称
     */
    @TableField(value = "sensitive_act")
    @NotBlank(message = "敏感行为名称不能为空")
    @ApiModelProperty(value = "敏感行为名称")
    @Excel(name = "敏感行为", sort = 3)
    private String sensitiveAct;

    /**
     * 删除状态
     */
    @TableField(value = "del_flag")
    @ApiModelProperty(value = "1 已删除 0 未删除")
    private Integer delFlag;


    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden = true)
    @Excel(name = "操作时间", sort = 4)
    private Date createTime;

    /**
     * 创建者
     */
    @TableField(value = "create_by")
    @ApiModelProperty(hidden = true)
    private String createBy;

    /**
     * 企业id
     */
    @TableField(value = "corp_id")
    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("主部门id")
    private Long mainDepartment;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("operatorId", getOperatorId())
                .append("operator", getOperator())
                .append("operateTargetId", getOperateTargetId())
                .append("operateTarget", getOperateTarget())
                .append("sensitiveActId", getSensitiveActId())
                .append("sensitiveAct", getSensitiveAct())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("corpId", getCorpId())
                .toString();
    }
}
