package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： CustomerSopVO
 *
 * @author 佚名
 * @date 2021/12/2 21:41
 */
@ApiModel("客户信息VO CustomerSopVO")
@Data
public class CustomerSopVO {
    @ApiModelProperty(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "客户头像")
    private String headImageUrl;

    @ApiModelProperty("所属员工")
    private String userName;

    @ApiModelProperty(value = "员工主部门名称")
    private String mainDepartmentName;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    private Integer type;

    @ApiModelProperty(value = "客户企业全称")
    private String corpFullName;
}
