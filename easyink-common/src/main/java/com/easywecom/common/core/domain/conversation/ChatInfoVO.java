package com.easywecom.common.core.domain.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天数据VO
 *
 * @author tigger
 * 2022/1/25 16:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatInfoVO extends ChatBodyVO {
    private List<String> tolist;

    private Long msgtime;

    private String msgid;

    private String action;

    private String from;

    private String msgtype;

    private String roomid;

    private Long seq;

    /***/
    private String voiceid; //音频存档消息
    private String voipid; //音频共享文档消息
    private Boolean isRevoke;
    private Object fromInfo;
    private Object toListInfo;
    private Object roomInfo;

}
