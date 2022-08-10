package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: QueryPresTagGroupDTO
 *
 * @author: 1*+
 * @date: 2021-11-01 17:49
 */
@Data
@ApiModel("标签入群列表查询条件")
public class QueryPresTagGroupDTO {

    @ApiModelProperty("任务名")
    private String taskName;
    @ApiModelProperty("发送类型")
    private Integer sendType;
    @ApiModelProperty("创建人")
    private String createBy;
    @ApiModelProperty("开始日期")
    private String beginTime;
    @ApiModelProperty("结束日期")
    private String endTime;

}
