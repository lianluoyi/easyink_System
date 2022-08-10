package com.easywecom.wecom.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 社群运营 老客户标签建群
 * 类名： WePresTagGroupTaskDTO
 *
 * @author 佚名
 * @date 2021/9/30 15:30
 */
@Data
@ApiModel("标签建群DTO")
public class WePresTagGroupTaskDTO {

    @NotNull(message = "任务名称不能为空")
    @Size(max = 32, message = "任务名称长度已超出限制")
    @ApiModelProperty("任务名称")
    private String taskName;

    @NotNull(message = "发送方式不能为空")
    @ApiModelProperty("发送方式 0: 企业群发 1：个人群发")
    private Integer sendType;

    @Size(max = 2000, message = "加群引导语长度已超出限制")
    @ApiModelProperty("加群引导语")
    private String welcomeMsg;

    @ApiModelProperty("群活码id")
    private Long groupCodeId;

    @ApiModelProperty("客户标签")
    private List<String> tagList;

    @ApiModelProperty("选择员工")
    private List<String> scopeList;

    @ApiModelProperty("发送范围 0: 全部客户 1：部分客户")
    private Integer sendScope;

    @ApiModelProperty("发送性别 0: 全部 1： 男 2： 女 3：未知")
    private Integer sendGender;

    @ApiModelProperty("目标客户被添加起始时间")
    private String cusBeginTime;

    @ApiModelProperty("目标客户被添加结束时间")
    private String cusEndTime;
}
