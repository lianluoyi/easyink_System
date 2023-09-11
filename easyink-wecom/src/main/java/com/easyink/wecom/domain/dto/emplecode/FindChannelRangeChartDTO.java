package com.easyink.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 查询获客链接-趋势图和渠道新增客户排行DTO
 *
 * @author lichaoyu
 * @date 2023/8/24 17:59
 */
@Data
public class FindChannelRangeChartDTO {

    @ApiModelProperty("企业ID")
    private String corpId;
    @ApiModelProperty("获客链接id")
    private String empleCodeId;
    @ApiModelProperty("开始时间")
    private String beginTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("渠道ID")
    private String channelId;
    @ApiModelProperty("自定义渠道ID列表")
    private List<Long> channelIdList;
}
