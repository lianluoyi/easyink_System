package com.easyink.wecom.domain.dto.form.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 企业第三方服务推送配置DTO
 *
 * @author easyink
 * @date 2024-01-01
 */
@ApiModel("企业第三方服务推送配置DTO")
@Data
public class WeCorpThirdPartyConfigDTO {

    @ApiModelProperty(value = "企业ID", required = true)
    private String corpId;

    @ApiModelProperty(value = "第三方推送URL", required = true)
    @NotNull(message = "推送URL不能为null")
    private String pushUrl;


    @ApiModelProperty("状态(0:停用 1:启用)")
    private Integer status;

}
