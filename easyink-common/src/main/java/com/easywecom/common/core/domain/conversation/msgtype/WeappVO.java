package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 小程序VO
 *
 * @author tigger
 * 2022/2/8 14:30
 **/
@Data
public class WeappVO extends AttachmentBaseVO {
    /**消息标题*/
    private String title;
    /**消息描述*/
    private String description;
    /**用户名称*/
    private String username;
    /**小程序名称*/
    private String displayname;
}
