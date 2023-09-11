package com.easyink.wecom.domain.dto.emplecode;

import com.easyink.common.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * 获客链接-详情-数据统计-员工维度查询DTO
 *
 * @author lichaoyu
 * @date 2023/8/25 9:29
 */
@Data
public class FindAssistantDetailStatisticCustomerDTO {

    @ApiModelProperty("企业ID")
    private String corpId;
    @ApiModelProperty("客户名称")
    private String name;
    @ApiModelProperty("员工id列表，多个用，隔开")
    private String userIds;
    @ApiModelProperty("获客链接id")
    private String empleCodeId;
    @ApiModelProperty("渠道id")
    private Long channelId;
    @ApiModelProperty("开始时间，格式为YYYY-MM-DD HH:MM:00")
    private String beginTime;
    @ApiModelProperty("结束时间，格式为YYYY-MM-DD HH:MM:59")
    private String endTime;
    @ApiModelProperty("渠道id列表")
    private List<Long> channelIdList;

    public String getBeginTime() {
        return DateUtils.parseBeginMinute(beginTime);
    }

    public String getEndTime() {
        return DateUtils.parseEndMinute(endTime);
    }
}
