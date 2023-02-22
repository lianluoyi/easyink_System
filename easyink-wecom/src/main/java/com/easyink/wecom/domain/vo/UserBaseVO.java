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

    @ApiModelProperty("员工所属部门")
    @Excel(name = "员工所属部门", sort = 2)
    private String departmentName;
}
