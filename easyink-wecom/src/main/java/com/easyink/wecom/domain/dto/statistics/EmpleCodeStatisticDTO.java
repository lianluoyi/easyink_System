package com.easyink.wecom.domain.dto.statistics;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 活码统计-查询DTO
 *
 * @author lichaoyu
 * @date 2023/7/4 10:01
 */
@Data
public class EmpleCodeStatisticDTO extends BaseEntity {

    @ApiModelProperty("企业ID")
    private String corpId;

    @ApiModelProperty("员工活码idList")
    private List<Long> empleCodeIdList;

    @ApiModelProperty("部门idList")
    private List<String> departmentIds;

    @ApiModelProperty("员工idList")
    private List<String> userIds;

    @ApiModelProperty("当前时间 格式: YYYY-MM-DD HH:MM:SS")
    private String nowTime;

    @ApiModelProperty("开始时间 格式: YYYY-MM-DD")
    private String beginDate;

    @ApiModelProperty("结束时间 格式: YYYY-MM-DD")
    private String endDate;
}
