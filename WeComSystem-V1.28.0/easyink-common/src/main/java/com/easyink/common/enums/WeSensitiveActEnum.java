package com.easyink.common.enums;

import lombok.Getter;

@Getter
public enum WeSensitiveActEnum {
    /**
     * 开启记录敏感行为
     */
    OPEN(1, "", "开启"),

    /**
     * 删除拉黑好友
     */
    DELETE(2, "deletefriend", "拉黑/删除客户"),

    /**
     * 发名片
     */
    SEND_CARD(3, "card", "员工发送名片"),

    /**
     * 发红包
     */
    SEND_REDPACK(4, "redpacket", "员工/客户发送红包"),

    /**
     * 企微发红包
     */
    SEND_EXTERNAL_REDPACK(4, "external_redpacket", "员工/客户发送红包"),

    /**
     * 关闭记录敏感行为
     */
    CLOSE(0, "", "关闭");


    private final Integer code;
    private final String name;
    private final String info;

    WeSensitiveActEnum(Integer code, String name, String info) {
        this.code = code;
        this.name = name;
        this.info = info;
    }

}
