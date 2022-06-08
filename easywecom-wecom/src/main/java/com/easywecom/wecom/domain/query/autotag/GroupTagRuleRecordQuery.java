package com.easywecom.wecom.domain.query.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 群记录query
 *
 * @author tigger
 * 2022/3/1 17:23
 **/
@Data
public class GroupTagRuleRecordQuery extends TagRuleRecordQuery {

    @ApiModelProperty("客户群")
    private String groupName;
}
