package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 不同意会话聊天VO
 *
 * @author tigger
 * 2022/2/8 15:14
 **/
@Data
public class DisagreeVO {
    /**不同意协议者的userid，外部企业默认为external_userid*/
    private String userid;
    /**不同意协议的时间，utc时间，ms单位。*/
    private Long disagree_time;
}
