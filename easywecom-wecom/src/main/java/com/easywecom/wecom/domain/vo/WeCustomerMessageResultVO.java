package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 消息发送结果VO
 *
 * @author 佚名
 * @date 2021/11/12 16:30
 */
@Data
@ApiModel("消息发送结果VO<WeCustomerMessageResultVO>")
public class WeCustomerMessageResultVO {

    @ApiModelProperty("发送员工名称")
    private String userName;
    @ApiModelProperty("发送员工Id")
    private String userId;
    @ApiModelProperty("头像url")
    private String headImageUrl;
    @ApiModelProperty("客户名称列表，通过`、`进行分隔")
    private String customers;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("任务名")
    private String taskName;
    @ApiModelProperty("发送状态  0-未执行 1-已执行 2-发送成功 3-发送失败")
    private String sendStatus;


}
