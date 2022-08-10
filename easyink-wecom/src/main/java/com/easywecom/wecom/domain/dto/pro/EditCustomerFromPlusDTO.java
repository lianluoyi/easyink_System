package com.easywecom.wecom.domain.dto.pro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名: 修改企微PRO的客户资料请求参数实体
 *
 * @author : silver_chariot
 * @date : 2021/11/2 15:21
 */
@Data
@ApiModel("修改企微PRO的客户资料请求参数")
public class EditCustomerFromPlusDTO {

    @ApiModelProperty(value = "企业ID",required = true)
    @NotBlank(message = "参数缺失")
    private String corpId;

    @ApiModelProperty(value = "成员ID",required = true)
    @NotBlank(message = "参数缺失")
    private String userId;

    @ApiModelProperty(value = "客户头像",required = true)
    @NotBlank(message = "参数缺失")
    private String avatar;

    @ApiModelProperty(value = "客户备注")
    private String remark;

    @ApiModelProperty(value = "客户备注手机")
    private String phone;

    @ApiModelProperty(value = "描述")
    private String desc;

}
