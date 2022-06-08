package com.easywecom.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * sop员工执行记录
 *
 * @author tigger
 * 2021/12/7 13:49
 **/
@Data
@ApiModel("sop员工执行记录")
public class FindWeSopExecutedUsersDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @NotNull(message = "sopId不能为空")
    @ApiModelProperty("sopId")
    private String sopId;

    @ApiModelProperty("员工名称")
    private String userName;

    @ApiModelProperty("页码")
    private Integer pageNum;
    @ApiModelProperty("每页大小")
    private Integer pageSize;
}
