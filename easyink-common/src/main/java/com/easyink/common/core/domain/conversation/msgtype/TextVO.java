package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 文本VO
 *
 * @author tigger
 * 2022/1/25 17:40
 **/
@Data
public class TextVO extends AttachmentBaseVO{
    private String content;
}
