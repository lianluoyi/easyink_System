package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： 群发提醒员工DTO
 *
 * @author 佚名
 * @date 2021/11/17 14:22
 */
@Data
@ApiModel("群发提醒员工DTO")
public class WeCustomerMessageToUserDTO {
    @ApiModelProperty(value = "企业id", hidden = true)
    private String corpId;

    @ApiModelProperty(value = "员工id", required = true)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "客户/群名称用“、”隔开", required = true)
    @NotBlank
    private String customers;

    @ApiModelProperty(value = "群发类型 0 发给客户 1 发给客户群", required = true)
    @NotBlank
    private String pushType;
}
