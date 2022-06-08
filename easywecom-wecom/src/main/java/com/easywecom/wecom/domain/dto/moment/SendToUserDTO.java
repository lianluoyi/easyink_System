package com.easywecom.wecom.domain.dto.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名： SendToUserDTO
 *
 * @author 佚名
 * @date 2022/1/14 19:45
 */
@Data
@ApiModel("朋友圈发送提醒信息DTO")
public class SendToUserDTO {
    @ApiModelProperty(value = "员工id列表",required = true)
    @NotNull(message = "员工id不能为空")
    List<String> userIds;
    @ApiModelProperty(value = "发布类型（0：企业 1：个人）",required = true)
    @Max(1)
    @Min(0)
    private Integer type;
    @ApiModelProperty(value = "发送时间",required = true)
    @NotBlank(message = "发送时间不能为空")
    String sendTime;
    @ApiModelProperty(value = "朋友圈任务id",required = true)
    @NotNull(message = "任务id不能为空")
    Long momentTaskId;
}
