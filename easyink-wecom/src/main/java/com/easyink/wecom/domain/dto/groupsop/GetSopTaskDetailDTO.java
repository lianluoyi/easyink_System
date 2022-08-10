package com.easyink.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名：GetSopTaskDetailDTO
 *
 * @author Society my sister Li
 * @date 2021-12-07 13:42
 */
@Data
@ApiModel("获取任务详情")
public class GetSopTaskDetailDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(hidden = true)
    private String userId;

    @ApiModelProperty("任务类型. 0:客户SOP,1:群SOP,2:群日历")
    private Integer type;

    @ApiModelProperty(hidden = true)
    private Date startTime;

    @ApiModelProperty(hidden = true)
    private Date endTime;

    @ApiModelProperty("分页时需要传该参数")
    private Long detailId;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页数量")
    private Integer pageSize;

    @ApiModelProperty("是否已执行")
    private Boolean isFinish;

    @ApiModelProperty("规则ID")
    private Long ruleId;
}
