package com.easyink.wecom.domain.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： OrderHadlerDTO
 *
 * @author 佚名
 * @date 2021/12/13 20:19
 */
@Data
@ApiModel("工单状态变更")
public class OrderHadlerDTO {
    @ApiModelProperty(value = "网点id", hidden = true)
    private String networkId;
    @ApiModelProperty("微信消息id")
    private String messageId;
    @ApiModelProperty("客服id")
    @NotBlank
    private String userId;
    @ApiModelProperty("工单系统id")
    @NotBlank
    private String orderId;
    @ApiModelProperty("变更后工单状态(固定值:处理中,已处理)")
    @NotBlank
    private String orderStatus;
    @ApiModelProperty("文本内容")
    private String content;
}
