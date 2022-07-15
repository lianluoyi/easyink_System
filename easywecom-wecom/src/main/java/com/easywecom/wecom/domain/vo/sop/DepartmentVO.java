package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 部门列表VO
 *
 * @author wx
 * 2022/6/30 20:24
 **/
@Data
@ApiModel("部门实体VO")

public class DepartmentVO {
    @ApiModelProperty("部门id")
    private String departmentId;
    @ApiModelProperty("部门名称")
    private String departmentName;
    @ApiModelProperty("父部门id")
    private String parentId;
    @ApiModelProperty("父部门名称")
    private String mainDepartmentName;
}