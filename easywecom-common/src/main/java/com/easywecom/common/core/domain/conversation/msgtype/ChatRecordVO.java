package com.easywecom.common.core.domain.conversation.msgtype;

import com.easywecom.common.core.domain.conversation.ChatBodyVO;
import lombok.Data;

import java.util.List;

/**
 * 聊天记录VO
 *
 * @author tigger
 * 2022/1/26 16:09
 **/
@Data
public class ChatRecordVO extends AttachmentBaseVO {
    private String title;
    private List<ChatRecordItem> item;

    @Data
    public static class ChatRecordItem extends ChatBodyVO {
        private String type;
        private Integer msgtime;
        private Boolean from_chatroom;
    }

}
