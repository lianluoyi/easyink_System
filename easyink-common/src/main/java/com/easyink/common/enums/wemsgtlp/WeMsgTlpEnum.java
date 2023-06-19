package com.easyink.common.enums.wemsgtlp;

/**
 * 欢迎语素材类型定义枚举类
 *
 * @author lichaoyu
 * @date 2023/6/13 9:53
 */
public enum WeMsgTlpEnum {
    /**
     * 文本
     */
    TEXT(0),
    /**
     * 图片
     */
    IMAGE(1),
    /**
     * 链接
     */
    LINK(2),
    /**
     * 小程序
     */
    MINI_PROGRAM(3),
    /**
     * 文件
     */
    FILE(4),
    /**
     * 视频媒体文件
     */
    VIDEO(5),
    /**
     * 雷达
     */
    RADAR(7);
    private final int value;

    WeMsgTlpEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
