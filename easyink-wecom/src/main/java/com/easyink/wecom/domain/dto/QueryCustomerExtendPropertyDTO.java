package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 客户扩展属性查询参数
 *
 * @author : silver_chariot
 * @date : 2021/11/10 20:03
 */
@Data
@ApiModel("客户扩展属性列表查询参数")
public class QueryCustomerExtendPropertyDTO {

    @ApiModelProperty(value = "字段名称")
    private String name;
    @ApiModelProperty(value = "状态1启用0停用")
    private Boolean status;
    @ApiModelProperty(value = "是否必填1必填0非必填")
    private Boolean required;
    @ApiModelProperty(value = "专属活码是否必填1必填0非必填")
    private Boolean liveCodeRequired;
    /**
     * 企业ID
     */
    private String corpId;
}
