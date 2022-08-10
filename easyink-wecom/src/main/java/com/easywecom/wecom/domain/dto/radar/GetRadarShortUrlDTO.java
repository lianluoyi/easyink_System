package com.easywecom.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ClassName： GetRadarShortUrlDTO
 *
 * @author wx
 * @date 2022/7/24 16:17
 */
@Data
@ApiModel("侧边栏获取雷达短链DTO")
public class GetRadarShortUrlDTO {

    @ApiModelProperty("雷达id")
    @NotNull
    private Long radarId;

    @ApiModelProperty("员工id")
    @NotBlank
    private String userId;
}
