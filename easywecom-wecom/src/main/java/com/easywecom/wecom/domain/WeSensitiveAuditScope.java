package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@TableName(value = "we_sensitive_audit_scope")
@ApiModel(value = "敏感词审计范围")
public class WeSensitiveAuditScope implements Serializable {
    private static final long serialVersionUID = 7696067707890730540L;

    @ApiModelProperty(value = "审计范围id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "敏感词表主键")
    @TableField("sensitive_id")
    private Long sensitiveId;

    @ApiModelProperty(value = "审计范围类型, 1 组织机构 2 成员")
    @TableField("scope_type")
    private Integer scopeType;

    @ApiModelProperty(value = "审计对象id")
    @TableField("audit_scope_id")
    private String auditScopeId;

    @ApiModelProperty(value = "审计对象名称")
    @TableField("audit_scope_name")
    private String auditScopeName;

    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("scopeType", getScopeType())
                .append("auditScopeId", getAuditScopeId())
                .append("auditScopeName", getAuditScopeName())
                .append("corpId", getCorpId())
                .toString();
    }
}
