package com.easyink.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： SearchRadarChannelDTO
 *
 * @author wx
 * @date 2022/7/19 16:30
 */
@Data
@ApiModel("查询雷达渠道DTO")
public class SearchRadarChannelDTO {

    @ApiModelProperty("雷达ID")
    private String radarId;

    @ApiModelProperty(value = "渠道名称")
    private String name;

    @ApiModelProperty(value = "corpId")
    private String corpId;
}
