package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 同意会话聊天VO
 *
 * @author tigger
 * 2022/2/8 15:16
 **/
@Data
public class AgreeVO {

    /**同意协议者的userid，外部企业默认为external_userid*/
    private String userid;
    /**同意协议的时间，utc时间，ms单位。*/
    private Long agree_time;
}
