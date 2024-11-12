package com.easyink.wecom.domain.enums.third;

import lombok.Getter;

/**
 * 海德堡消息枚举
 * @author tigger
 * 2024/11/5 15:47
 **/
@Getter
public enum HaiderbaoMsgTypeEnum {
    /**
     * 未知
     */
    TEXT(1010,"text"),
    FILE(1020,"file"),
    IMAGE(1021,"image"),
    VOICE(1040,"voice"),
    VIDEO(1081,"video"),
    OTHER(9999,"other"),
    ;
    private final Integer origin;
    private final String target;

    HaiderbaoMsgTypeEnum(Integer origin, String target) {
        this.origin = origin;
        this.target = target;
    }


    public static HaiderbaoMsgTypeEnum getEnumByOriginType(Integer origin) {
        for (HaiderbaoMsgTypeEnum value : values()) {
            if (value.origin.equals(origin)) {
                return value;
            }
        }
        return null;
    }
}
