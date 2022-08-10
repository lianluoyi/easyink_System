package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sop规则执行记录
 *
 * @author tigger
 * 2021/12/6 14:53
 **/
@Data
public class WeSopExecutedRulesVO extends AbstractExecuteVO{
    @ApiModelProperty("ruleId")
    private Integer ruleId;
    @ApiModelProperty("sopId")
    private String sopId;
    @ApiModelProperty("规则类型 提醒类型\\r\\n0：xx小时xx分钟提醒，1：xx天xx:xx提醒，2：每天xx:xx提醒，3：每周周x的xx:xx提醒，4：每月x日xx:xx提醒")
    private Integer alertType;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("提醒时间 第几天")
    private Integer alertDay;
    @ApiModelProperty("提醒时间 小时:分钟")
    private String alertTime;


}
