package com.easywecom.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 素材分组数据初始化枚举定义
 *
 * @author Society my sister Li
 * @date 2021/9/22
 */
public enum WeCategoryMediaTypeEnum {

    //素材分组 素材类型和 名称定义

    IMAGE(0, "海报"),
    VIDEO(2, "视频"),
    FILE(3, "文件"),
    LINK(5, "链接"),
    MINI_APP(6, "小程序"),
    ;
    /**
     * 素材类型
     */
    @Getter
    private Integer mediaType;

    /**
     * 名称
     */
    @Getter
    private String name;

    WeCategoryMediaTypeEnum(Integer mediaType, String name) {
        this.mediaType = mediaType;
        this.name = name;
    }

    /**
     * 获取所有mediaType
     *
     * @return List<Integer>
     */
    public static List<Integer> getMediaTypeList() {
        List<Integer> list = new ArrayList<>();
        for (WeCategoryMediaTypeEnum weCategoryMediaTypeEnum : values()) {
            list.add(weCategoryMediaTypeEnum.getMediaType());
        }
        return list;
    }
}
