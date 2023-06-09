package com.easyink.wecom.domain.dto.pro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名: 企微PRO接口：获取客户详情请求参数实体
 *
 * @author : silver_chariot
 * @date : 2021/11/2 11:41
 */
@Data
@ApiModel("获取客户详情请求参数")
public class QueryCustomerFromPlusDTO {

    @ApiModelProperty(value = "企业ID" , required = true)
    @NotBlank(message = "参数缺失")
    private String corpId;

    @ApiModelProperty(value = "成员ID" , required = true)
    @NotBlank(message = "参数缺失")
    private String userId;

    @ApiModelProperty(value = "客户头像" , required = true)
    @NotBlank(message = "参数缺失")
    private String avatar;

}
