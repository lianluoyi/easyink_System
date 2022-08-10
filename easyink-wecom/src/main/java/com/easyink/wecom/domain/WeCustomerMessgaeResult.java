package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群发消息  微信消息发送结果表 we_customer_messgaeResult
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_customer_messgaeResult")
public class WeCustomerMessgaeResult extends BaseEntity {

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("messgae_result_id")
    private Long messgaeResultId;

    @ApiModelProperty(value = "微信消息表id")
    @TableField("message_id")
    private Long messageId;

    @ApiModelProperty(value = "外部联系人userid")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty(value = "外部客户群id")
    @TableField(value = "chat_id")
    private String chatId;

    @ApiModelProperty(value = "企业服务人员的userid")
    @TableField("userid")
    private String userid;

    @ApiModelProperty(value = "发送状态 0-未发送 1-已发送 2-因客户不是好友导致发送失败 3-因客户已经收到其他群发消息导致发送失败")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "")
    @TableField("send_time")
    private String sendTime;

    @ApiModelProperty(value = "外部联系人名称")
    @TableField("external_name")
    private String externalName;

    @ApiModelProperty(value = "企业服务人员的名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "0 发给客户 1 发给客户群 2 定时发送")
    @TableField("send_type")
    private String sendType;

    @ApiModelProperty(value = "定时发送时间")
    @TableField("setting_time")
    private String settingTime;

    @ApiModelProperty(value = "")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "外部客户群名称")
    @TableField("chat_name")
    private String chatName;


    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;


}
