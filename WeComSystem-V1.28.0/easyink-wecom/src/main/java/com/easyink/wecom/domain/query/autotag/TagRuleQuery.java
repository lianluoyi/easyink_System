package com.easyink.wecom.domain.query.autotag;

import com.easyink.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 标签规则查询query
 *
 * @author tigger
 * 2022/2/28 16:48
 **/
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TagRuleQuery extends PageEntity {
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("标签id列表")
    private List<String> tagIdList;
    @ApiModelProperty("创建人")
    private String createBy;
    @ApiModelProperty("启用禁用状态 0:禁用1:启用")
    private Boolean status;

    private String corpId;
}
