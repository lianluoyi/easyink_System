package com.easywecom.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部门列表VO
 *
 * @author wx
 * 2022/6/20 16:00
 **/
@Data
@ApiModel("自动标签-部门实体VO")
public class TagRuleDepartmentInfoVO {
    @ApiModelProperty("部门id")
    private String departmentId;
    @ApiModelProperty("部门名称")
    private String departmentName;
    @ApiModelProperty("父部门id")
    private String parentId;
    @ApiModelProperty("父部门名称")
    private String mainDepartmentName;
}
