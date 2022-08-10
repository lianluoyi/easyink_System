package com.easyink.common.core.domain.conversation.msgtype;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会议邀请VO
 *
 * @author tigger
 * 2022/2/8 14:39
 **/
@Data
public class MeetingVO extends AttachmentBaseVO{
    @ApiModelProperty("会议主题")
    private String topic;

    @ApiModelProperty("红包总个数")
    private Long startTime;

    @ApiModelProperty("会议结束时间")
    private Long endTime;

    @ApiModelProperty("会议地址")
    private String address;

    @ApiModelProperty("会议备注")
    private String remarks;

    @ApiModelProperty("会议消息类型。101发起会议邀请消息、102处理会议邀请消息")
    private String meetingType;

    @ApiModelProperty("会议id。方便将发起、处理消息进行对照")
    private Long meetingId;

    @ApiModelProperty("会议邀请处理状态。1 参加会议、2 拒绝会议、3 待定、4 未被邀请、5 会议已取消、6 会议已过期、7 不在房间内。" +
            "只有meetingtype为102的时候此字段才有内容。")
    private Integer status;
}
