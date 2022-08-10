package com.easyink.wecom.domain.query;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author admin
 * @version 1.0
 * @date 2021/1/4 21:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WeSensitiveHitQuery extends BaseEntity {
    /**
     * 1:组织机构id,2:成员id
     */
    @ApiModelProperty(value = "审计范围类型, 1 组织机构 2 成员")
    @NotNull(message = "scopeType参数不能为空")
    @Min(value = 1L, message = "scopeType参数不合法[1,2]")
    @Min(value = 2L, message = "scopeType参数不合法[1,2]")
    private Integer scopeType;

    /**
     * 审计对象id
     */
    @ApiModelProperty(value = "审计范围id")
    private String auditScopeId;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    private String keyword;
}
