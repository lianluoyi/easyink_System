package com.easyink.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： SopUserVO
 *
 * @author 佚名
 * @date 2021/12/3 10:05
 */
@ApiModel("sop员工vo")
@Data
public class SopUserVO {
    @ApiModelProperty(value = "员工id")
    private String userId;
    @ApiModelProperty(value = "员工头像")
    private String headImageUrl;
    @ApiModelProperty(value = "员工名称")
    private String userName;
    @ApiModelProperty(value = "员工主部门名称")
    private String mainDepartmentName;
    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    private Integer type;

    @ApiModelProperty(value = "客户企业全称")
    private String corpFullName;
}
