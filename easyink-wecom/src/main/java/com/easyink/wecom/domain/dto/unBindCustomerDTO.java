package com.easyink.wecom.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： 企业客户群与工单客户解绑关系DTO
 *
 * @author 佚名
 * @date 2021/12/15 13:59
 */
@Data
@ApiModel("企业客户群与工单客户解绑关系DTO")
public class unBindCustomerDTO {
    @ApiModelProperty(value = "外部群ID", required = true)
    @NotBlank
    private String chatId;

    @ApiModelProperty(value = "企业ID", hidden = true)
    @JsonIgnore
    private String corpId;
}
