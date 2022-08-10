package com.easywecom.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 员工列表VO
 *
 * @author tigger
 * 2022/2/28 15:00
 **/
@Data
public class TagRuleUserListVO extends TagRuleInfoVO{
    @ApiModelProperty("员工详情列表")
    private List<TagRuleUserInfoVO> userList;

    @ApiModelProperty("部门详情列表")
    private List<TagRuleDepartmentInfoVO> departmentList;
}
