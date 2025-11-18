package com.easyink.wecom.domain.dto.map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 地图API配置DTO
 *
 * @author wx
 * @date 2023/8/5
 */
@Data
@ApiModel("地图API配置DTO")
public class WeMapConfigDTO {
    @ApiModelProperty(value = "企业id", required = true)
    private String corpId;

    @ApiModelProperty(value = "后端API密钥", required = true)
    @NotBlank(message = "后端API密钥不能为空")
    private String apiKey;
    
    @ApiModelProperty(value = "iframe API密钥", required = true)
    @NotBlank(message = "iframe API密钥不能为空")
    private String iframeApiKey;

    @ApiModelProperty(value = "地图类型：1-腾讯地图, 2-高德地图, 3-百度地图", required = true)
    @NotNull(message = "地图类型不能为空")
    private Integer mapType;

    @ApiModelProperty(value = "每日调用限制次数，NULL表示无限制")
    private List<ApiLimitDTO> dailyLimits;

    @ApiModelProperty(value = "状态 0：停用 1：启用", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiLimitDTO{
        @ApiModelProperty(value = "接口apicode")
        private Integer apiCode;
        @ApiModelProperty(value = "每日调用限制次数")
        private Integer dailyLimit;
    }
} 