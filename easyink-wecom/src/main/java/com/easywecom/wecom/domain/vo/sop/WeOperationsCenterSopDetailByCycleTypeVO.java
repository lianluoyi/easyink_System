package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SOP群执行详情 - 循环类型
 *
 * @author tigger
 * 2021/12/7 10:13
 **/
@Data
public class WeOperationsCenterSopDetailByCycleTypeVO extends WeOperationsCenterSopDetailChatVO {

    @ApiModelProperty("循环次数")
    private Integer cycleNum;
    @ApiModelProperty("已经执行")
    private Integer executed;
    @ApiModelProperty("未执行")
    private Integer unExecute;
}
