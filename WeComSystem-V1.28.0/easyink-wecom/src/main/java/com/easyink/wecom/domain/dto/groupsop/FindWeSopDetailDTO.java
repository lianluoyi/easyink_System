package com.easyink.wecom.domain.dto.groupsop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * sop详情
 *
 * @author tigger
 * 2021/12/6 11:18
 **/
@Data
@ApiModel("查询SOP详情")
public class FindWeSopDetailDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @NotNull(message = "sopId不能为空")
    @ApiModelProperty(value = "sopId",required = true)
    private Long sopId;

    @ApiModelProperty(value = "ruleId")
    private Long ruleId;

    @ApiModelProperty(value = "userId")
    private String userId;

    @ApiModelProperty("群名称")
    private String chatName;
    @ApiModelProperty("客户名称")
    private String customerName;
    @ApiModelProperty("群主名称")
    private String chatOwnerName;
    @ApiModelProperty("员工姓名")
    private String employName;

    @Max(value = 1,message = "请输入正确的执行状态")
    @Min(value = 0,message = "请输入正确的执行状态")
    @ApiModelProperty("执行状态 0：未执行，1：已执行")
    private Integer finishFlag;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private String beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页大小")
    private Integer pageSize;
}
