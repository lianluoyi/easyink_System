package com.easyink.wecom.domain.dto.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 客户活跃度查询DTO
 *
 * @author wx
 * 2023/2/14 14:16
 **/
@Data
public class CustomerActivityDTO extends StatisticsDTO{

    @ApiModelProperty("发送时间的开始时间")
    private String sendStartTime;

    @ApiModelProperty("发送时间的结束时间")
    private String sendEndTime;
}
