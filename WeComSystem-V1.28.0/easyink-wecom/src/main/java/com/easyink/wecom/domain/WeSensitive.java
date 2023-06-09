package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 敏感词实体
 *
 * @author 佚名
 * @date 2021-7-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "we_sensitive")
@ApiModel(value = "敏感词")
public class WeSensitive extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 策略名称
     */
    @TableField(value = "strategy_name")
    @NotBlank(message = "策略名称不能为空")
    @ApiModelProperty(value = "策略名称")
    private String strategyName;

    /**
     * 匹配词
     */
    @TableField(value = "pattern_words")
    @ApiModelProperty(value = "匹配词，逗号分隔")
    @NotBlank(message = "匹配词不能为空")
    private String patternWords;

    /**
     * 审计范围，敏感词审计需要覆盖的用户或机构
     */
    @NotEmpty(message = "审计范围不能为空")
    @TableField(exist = false)
    @ApiModelProperty(value = "审计范围")
    private List<WeSensitiveAuditScope> auditUserScope;

    /**
     * 审计人id
     */
    @TableField(value = "audit_user_id")
    @ApiModelProperty(value = "审计人id")
    @NotBlank(message = "审计人id不能为空")
    private String auditUserId;

    /**
     * 审计人姓名
     */
    @TableField(value = "audit_user_name")
    @ApiModelProperty(value = "审计人姓名")
    @NotBlank(message = "审计人不能为空")
    private String auditUserName;

    @ApiModelProperty(value = "消息通知,1 开启 0 关闭")
    @TableField("alert_flag")
    private Integer alertFlag;

    /**
     * 删除状态
     */
    @TableField(value = "del_flag")
    @TableLogic
    @ApiModelProperty(value = "1 已删除 0 未删除")
    private Integer delFlag;

    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;

    /**
     * 搜索关键词
     */
    @ApiModelProperty(value = "搜索关键词")
    private String keyWord;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("strategyName", getStrategyName())
                .append("patternWords", getPatternWords())
                .append("auditUserId", getAuditUserId())
                .append("alertFlag", getAlertFlag())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("corpId", getCorpId())
                .toString();
    }
}
