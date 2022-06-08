package com.easywecom.wecom.domain.vo.sop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：GetTaskDetailByUserIdVO
 *
 * @author Society my sister Li
 * @date 2021-12-07 14:00
 */
@Data
@ApiModel("SOP执行任务数据")
public class GetSopTaskByUserIdVO {

    @ApiModelProperty("sopId")
    private Long sopId;

    @ApiModelProperty("规则ID")
    private Long ruleId;

    @ApiModelProperty("规则名称")
    private String name;

    @ApiModelProperty("提醒时间 yyyy-MM-dd")
    private String alertDate;

    @ApiModelProperty("员工userId")
    private String userId;

    @ApiModelProperty("sop类型")
    private String sopType;

    @ApiModelProperty("具体时间 HH:mm")
    private String alertTime;

    @JsonIgnore
    private Integer hour;

    @JsonIgnore
    private Integer minute;

    @ApiModelProperty("具体发送对象信息")
    private List<GetTaskDetailByUserIdVO> detailList;
}
