package com.easywecom.wecom.domain.query.autotag;

import com.easywecom.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 标签规则记录QUERY
 *
 * @author tigger
 * 2022/3/1 17:13
 **/
@Data
public class TagRuleRecordQuery extends PageEntity {
    @NotNull(message = "请选择规则id")
    @ApiModelProperty("规则id")
    private Long ruleId;
    @ApiModelProperty("客户名称")
    private String customerName;
    @ApiModelProperty("员工id列表")
    private List<String> userIdList;

    private String corpId;
}
