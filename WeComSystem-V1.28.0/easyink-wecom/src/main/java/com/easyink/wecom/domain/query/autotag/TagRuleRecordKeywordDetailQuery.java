package com.easyink.wecom.domain.query.autotag;

import com.easyink.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 触发关键词详情query
 *
 * @author tigger
 * 2022/3/2 15:32
 **/
@Data
public class TagRuleRecordKeywordDetailQuery extends PageEntity {
    @NotNull(message = "规则id不能为空")
    @ApiModelProperty("规则id")
    private Long ruleId;
    @NotBlank(message = "客户id不能为空")
    @ApiModelProperty("客户id")
    private String customerId;
    @NotBlank(message = "员工id不能为空")
    @ApiModelProperty("员工id")
    private String userId;
    @ApiModelProperty("关键词")
    private String keyword;

    private String corpId;
}
