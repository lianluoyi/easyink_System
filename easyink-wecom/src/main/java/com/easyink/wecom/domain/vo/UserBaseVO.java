package com.easyink.wecom.domain.vo;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工基本信息
 *
 * @author wx
 * 2023/2/14 9:17
 **/
@Data
@NoArgsConstructor
public class UserBaseVO {

    @ApiModelProperty("员工id")
    private String userId;

    @ApiModelProperty("员工名称")
    @Excel(name = "员工姓名", sort = 1)
    private String userName;

    @ApiModelProperty("员工头像地址url")
    private String userHeadImage;

    @ApiModelProperty("员工职务")
    @Excel(name = "员工职务", sort = 2)
    private String position;

    @ApiModelProperty("员工所属部门")
    @Excel(name = "员工所属部门", sort = 3)
    private String departmentName;

    @Excel(name = "所属上级部门", sort = 4)
    @ApiModelProperty("所属上级部门")
    private String parentDepartmentName;
}
