package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 类名： 消息发送结果DTO
 *
 * @author 佚名
 * @date 2021/11/18 14:17
 */

@ApiModel("消息发送结果DTO")
@Data
public class WeCustomerMessagePushResultDTO {
    @ApiModelProperty(value = "消息id", required = true)
    @NotNull(message = "消息id不能为空")
    private Long messageId;
    @ApiModelProperty(value = "发送状态 0-未执行 1-已执行 2-发送成功 3-发送失败", required = true)
    @NotBlank(message = "发送状态不能为空")
    private String sendStatus;
    @ApiModelProperty("客户名称")
    private String customerName;
    @ApiModelProperty("客户群名称")
    private String groupName;
    @ApiModelProperty("员工名称")
    private String userName;
    @ApiModelProperty(value = "企业id", hidden = true)
    private String corpId;
    @ApiModelProperty("每页条数")
    private Integer pageSize;
    @ApiModelProperty("页码")
    private Integer pageNum;


}
