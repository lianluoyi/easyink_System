package com.easywecom.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * sop规则执行记录
 *
 * @author tigger
 * 2021/12/6 14:46
 **/
@Data
@ApiModel("sop规则执行记录")
public class FindWeSopExecutedRulesDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @NotNull(message = "sopId不能为空")
    @ApiModelProperty("sopId")
    private String sopId;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("页码")
    private Integer pageNum;
    @ApiModelProperty("每页大小")
    private Integer pageSize;

}
