package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;


/**
 * 代办VO
 *
 * @author tigger
 * 2022/2/8 14:33
 **/
@Data
public class TodoVO extends AttachmentBaseVO{

    /**待办的来源文本*/
    private String title;
    /**待办的具体内容*/
    private Integer content;

}
