package com.easyink.wecom.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tigger
 * 2023/12/19 11:04
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageCountModel {


    /**
     * 群发消息id
     */
    private Long messageId;
    /**
     * 预计发送数量
     */
    private int expectSend;
    /**
     * 实际发送数量
     */
    private int actualSend;
}
