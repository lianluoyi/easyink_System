package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 络客侧边栏配置DTO
 *
 * @author wx
 * 2023/3/17 17:21
 **/
@Data
public class LockSidebarConfigDTO {

    @ApiModelProperty("络客app_id")
    @NotBlank(message = "appId不得为空")
    private String appId;

    @ApiModelProperty("络客app_secret")
    @NotBlank(message = "appSecret不得为空")
    private String appSecret;

}
