package com.easyink.wecom.domain.query.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新客记录query
 *
 * @author tigger
 * 2022/3/1 17:25
 **/
@Data
public class CustomerTagRuleRecordQuery extends TagRuleRecordQuery {

    @ApiModelProperty("开始时间")
    private String beginTime;
    @ApiModelProperty("结束时间")
    private String endTime;


}
