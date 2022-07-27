package com.easywecom.common.enums;


import com.easywecom.common.utils.StringUtils;

/**
 * 公共素材类型枚举
 *
 * @author tigger
 * 2022/1/5 9:45
 **/
public enum AttachmentTypeEnum {

    /**
     * 文本素材
     */
    TEXT(0, "text"),
    /**
     * 图片素材
     */
    IMAGE(1, "image"),
    /**
     * 链接素材
     */
    LINK(2, "link"),
    /**
     * 小程序素材
     */
    MINIPROGRAM(3, "miniprogram"),
    /**
     * 文件素材
     */
    FILE(4, "file"),
    /**
     * 视频媒体文件素材
     */
    VIDEO(5, "video"),

    /**
     * 雷达链接
     */
    RADAR(7, "radar");

    /**
     * 素材类型
     */
    private Integer messageType;

    /**
     * 数据值
     */
    private String typeStr;

    public Integer getMessageType() {
        return messageType;
    }

    public String getTypeStr() {
        return typeStr;
    }

    AttachmentTypeEnum(Integer messageType, String typeStr) {
        this.messageType = messageType;
        this.typeStr = typeStr;
    }

    /**
     * 根据messageType获取枚举
     */
    public static AttachmentTypeEnum getByMessageType(Integer type) {
        if (type == null) {
            return null;
        }
        for (AttachmentTypeEnum value : values()) {
            if (value.getMessageType().equals(type)) {
                return value;
            }
        }
        return null;
    }
    public static AttachmentTypeEnum getByTypeStr(String typeStr) {
        if (StringUtils.isEmpty(typeStr)) {
            return null;
        }
        for (AttachmentTypeEnum value : values()) {
            if (value.getTypeStr().equals(typeStr)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 将{@link GroupMessageType} 的素材枚举类型映射为为欢迎语素材枚举 {@link AttachmentTypeEnum}
     * 素材类型:0海报、1语音、2视频、3普通文件、4文本、5链接、6小程序
     */
    public static AttachmentTypeEnum mappingFromGroupMessageType(Integer typeNum) {
        if(typeNum == null){
            return null;
        }
        switch (typeNum) {
            case 0:
                return IMAGE;
            case 2:
                return VIDEO;
            case 3:
                return FILE;
            case 4:
                return TEXT;
            case 5:
                return LINK;
            case 6:
                return MINIPROGRAM;
            case 7:
                return RADAR;
            default:
                return null;
        }
    }

}
