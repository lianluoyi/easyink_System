package com.easywecom.common.core.domain.conversation.msgtype;

import com.easywecom.common.core.domain.conversation.ChatInfoVO;
import lombok.Data;

/**
 * 撤回VO
 *
 * @author tigger
 * 2022/1/26 11:33
 **/
@Data
public class RevokeVO extends AttachmentBaseVO{
    private String pre_msgid;

    /**保存上一条消息详情*/
    private ChatInfoVO content;
}
