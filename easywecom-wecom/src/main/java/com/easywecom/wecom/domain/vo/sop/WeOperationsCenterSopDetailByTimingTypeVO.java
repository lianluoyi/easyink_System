package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SOP群执行详情 - 定时类型
 *
 * @author tigger
 * 2021/12/6 10:37
 **/
@Data
public class WeOperationsCenterSopDetailByTimingTypeVO extends WeOperationsCenterSopDetailChatVO {

    @ApiModelProperty("是否已执行 0：未执行，1：已执行")
    private Integer isFinish;
    @ApiModelProperty("提醒时间")
    private String alertTime;
    @ApiModelProperty("完成时间")
    private String finishTime;
}
