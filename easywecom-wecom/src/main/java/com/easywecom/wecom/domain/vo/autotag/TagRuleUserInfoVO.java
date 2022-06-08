package com.easywecom.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工详情VO
 *
 * @author tigger
 * 2022/2/28 14:57
 **/
@Data
public class TagRuleUserInfoVO {
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("主部门")
    private String mainDepartmentName;
}
