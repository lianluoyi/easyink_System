package com.easywecom.wecom.domain.vo.autotag;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 规则详情VO
 *
 * @author tigger
 * 2022/2/28 14:54
 **/
@Data
public class TagRuleInfoVO {
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("创建人")
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("创建时间")
    private Date createTime;

}
