package com.easyink.common.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 群发消息 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息 7 雷达 用逗号隔开
 * 注意：修改type请修改 {@link AttachmentTypeEnum#mappingFromGroupMessageType(java.lang.Integer)}
 */
@SuppressWarnings("all")
@Getter
@Slf4j
public enum GroupMessageType {


    /**
     * 图片海报消息
     */
    IMAGE("0", "image"),
    /**
     * 视频
     */
    VIDEO("2", "video"),
    /**
     * 文件
     */
    FILE("3", "file"),
    /**
     * 文本消息
     */
    TEXT("4", "text"),
    /**
     * 链接消息
     */
    LINK("5", "link"),
    /**
     * 小程序消息
     */
    MINIPROGRAM("6", "miniprogram"),

    /**
     * 雷达消息
     */
    RADAR("7", "radar");
    /**
     * 媒体类型
     */
    String messageType;

    /**
     * 数据值
     */
    String type;

    GroupMessageType(String type, String messageType) {
        this.type = type;
        this.messageType = messageType;
    }

    public static Optional<GroupMessageType> of(String type) {
        return Stream.of(values()).filter(s -> s.type.equals(type)).findFirst();
    }

    /**
     * 根据type查询枚举值
     *
     * @param type type
     * @return GroupMessageType
     */
    public static GroupMessageType getGroupMessageTypeByType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        for (GroupMessageType groupMessageType : values()) {
            if (groupMessageType.getType().equals(type)) {
                return groupMessageType;
            }
        }
        return null;
    }

    /**
     * 判断是否为合法的类型
     *
     * @param type       附件数值
     * @param validTypes 规定的合法类型
     * @return
     */
    public static boolean isValidType(String type, GroupMessageType... validTypes) {
        if (StringUtils.isBlank(type)) {
            log.error("附件类型不合法: 附件类型为空");
            return false;
        }
        for (GroupMessageType groupMessageType : validTypes) {
            if (groupMessageType.getType().equals(type)) {
                return true;
            } else {
                log.error("附件类型不合法: unValidType={}", groupMessageType.getType());
            }
        }
        return false;
    }
}
