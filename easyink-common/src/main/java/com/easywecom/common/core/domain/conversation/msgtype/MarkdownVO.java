package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * MarkDown格式VO
 *
 * @author tigger
 * 2022/2/8 14:48
 **/
@Data
public class MarkdownVO extends AttachmentBaseVO{
    /**markdown消息内容，目前为机器人发出的消息*/
    private Info info;

    @Data
    public static class Info{
        private String content;
    }

}
