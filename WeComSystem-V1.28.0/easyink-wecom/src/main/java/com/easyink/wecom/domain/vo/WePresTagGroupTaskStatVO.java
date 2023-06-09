package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 类名: WePresTagGroupTaskStatVO
 *
 * @author: 1*+
 * @date: 2021-11-03 15:14
 */
@Data
@ApiModel("进群详情数据对象")
public class WePresTagGroupTaskStatVO {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("送达状态[0:未送达,1:已送达]")
    private String status;

    @ApiModelProperty("是否在群[0:不在群,1:在群]")
    private boolean isInGroup = false;

    @ApiModelProperty("客户备注")
    private String remark;

    @ApiModelProperty("所属员工部门名称")
    private String mainDepartmentName;

    @ApiModelProperty("所属员工ID")
    private String userId;

    @ApiModelProperty("所属员工名")
    private String username;

    @ApiModelProperty("客户ID")
    private String externalUserid;

    @ApiModelProperty("所在客户群")
    private String inGroupName;

    @ApiModelProperty("头像")
    private String avatar;
}
