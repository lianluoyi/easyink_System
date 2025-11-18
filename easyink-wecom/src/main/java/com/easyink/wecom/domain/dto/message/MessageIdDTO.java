package com.easyink.wecom.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tigger
 * 2025/11/11
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageIdDTO {
    /**
     * 消息主键id
     */
    private String messageId;
    /**
     * 企微消息id
     */
    private String msgId;
}
