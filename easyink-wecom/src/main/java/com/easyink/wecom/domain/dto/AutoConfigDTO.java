package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名: 自动配置DTO
 *
 * @author: 1*+
 * @date: 2021-08-30 9:48
 */
@Data
@ApiModel("自动配置接口实体")
public class AutoConfigDTO {


    @NotBlank(message = "侧边栏域名不能为空")
    @ApiModelProperty(value = "侧边栏域名+端口", required = true)
    private String sidebarDomain;

    @NotBlank(message = "前端域名不能为空")
    @ApiModelProperty(value = "前端域名+端口", required = true)
    private String dashboardDomain;

    @NotBlank(message = "后端域名不能为空")
    @ApiModelProperty(value = "后端域名+端口", required = true)
    private String weComSystemDomain;

    @ApiModelProperty(value = "二维码Key", required = true)
    @NotBlank(message = "二维码Key不能为空")
    private String qrcodeKey;


}
