package com.easyink.wecom.domain.dto.statistics;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工服务日期维度查询DTO
 *
 * @author zhaorui
 * 2023/4/20 16:03
 **/
@Data
public class UserServiceTimeDTO {

    @ApiModelProperty("员工id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("用户id")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty("员工发送消息数量")
    @TableField("user_send_message_cnt")
    private Integer userSendMessageCnt;

    @ApiModelProperty("客户发送消息数量")
    @TableField("external_user_send_message_cnt")
    private Integer externalUserSendMessageCnt;
    @ApiModelProperty("发送消息时间")
    @TableField("send_time")
    private String sendTime;

    @ApiModelProperty("当天收到客户消息到首次回复客户时间间隔（单位分钟）")
    @TableField("first_reply_time_interval_alter_receive")
    private Integer firstReplyTimeIntervalAlterReceive;

    @ApiModelProperty("是否有三个轮次对话，1：有，0：无")
    @TableField("three_rounds_dialogue_flag")
    private Integer threeRoundsDialogueFlag;

    @ApiModelProperty("对话是否由员工主动发起，1：是，0：否")
    @TableField("user_active_dialogue")
    private Integer userActiveDialogue;

    @ApiModelProperty("评价分数")
    @TableField("score")
    private Integer score;
}
