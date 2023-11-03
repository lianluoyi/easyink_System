package com.easyink.common.enums.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聊天类型枚举
 *
 * @author lichaoyu
 * @date 2023/10/23 20:50
 */
@AllArgsConstructor
@Getter
public enum ChatTypeEnum {

    //是否为客户 0-内部 1-外部 2-群聊
    /**
     * 内部聊天（员工聊天）
     */
    INSIDE_CHAT(0),
    /**
     * 外部聊天（客户聊天）
     */
    OUTSIDE_CHAT(1),
    /**
     * 群聊聊天
     */
    GROUP_CHAT(2);

    /**
     * 聊天类型
     */
    private Integer type;
}
