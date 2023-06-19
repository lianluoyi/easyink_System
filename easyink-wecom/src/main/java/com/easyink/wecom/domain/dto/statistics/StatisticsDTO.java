package com.easyink.wecom.domain.dto.statistics;

import com.easyink.common.core.domain.RootEntity;
import com.easyink.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据统计查询DTO
 *
 * @author wx
 * 2023/2/14 14:04
 **/
@Data
public class StatisticsDTO extends RootEntity {

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 页码
     */
    private Integer pageNum;

    @ApiModelProperty("部门idList")
    private List<String> departmentIds;

    @ApiModelProperty("员工idList")
    private List<String> userIds;

    @ApiModelProperty("开始时间/添加时间的开始时间，格式为：YYYY-MM-DD 00:00:00")
    private String beginTime;

    @ApiModelProperty("结束时间/添加时间的结束时间，格式为：YYYY-MM-DD 23:59:59")
    private String endTime;

    @ApiModelProperty("结束时间，格式为：YYYY-MM-DD 00:00:00")
    private String endDate;

    public String getBeginTime() {
        return DateUtils.parseBeginDay(beginTime);
    }

    public String getEndTime() {
        return DateUtils.parseEndDay(endTime);
    }

    public String getEndDate() {
        return DateUtils.parseBeginDay(endTime);
    }

    /**
     * 企业id
     */
    private String corpId;

}
