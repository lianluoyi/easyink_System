package com.easywecom.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * ClassName： SearchRadarAnalyseDTO
 *
 * @author wx
 * @date 2022/7/20 15:36
 */
@Data
@ApiModel("搜索雷达数据统计DTO")
public class SearchRadarAnalyseDTO {

    @ApiModelProperty(value = "选择渠道名称")
    private String channelName;

    @ApiModelProperty(value = "雷达id")
    private Long radarId;

    @ApiModelProperty(value = "开始时间", required = true)
    private String beginTime;

    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;

    @ApiModelProperty(value = "员工ID")
    private String userId;

}
