package com.easywecom.wecom.domain.query.autotag;

import com.easywecom.common.exception.CustomException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 规则详情query
 *
 * @author tigger
 * 2022/2/28 19:05
 **/
@Data
public class RuleInfoQuery {

    @ApiModelProperty("规则id")
    private Long ruleId;

    private String corpId;

    public RuleInfoQuery(Long ruleId) {
        if (ruleId == null) {
            throw new CustomException("规则id不能为空");
        }
        this.ruleId = ruleId;
    }
}
