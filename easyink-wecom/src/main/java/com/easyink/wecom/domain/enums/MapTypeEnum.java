package com.easyink.wecom.domain.enums;

import lombok.Getter;

/**
 * 地图类型枚举
 * 
 * @author wx
 * @date 2023/8/5
 */
@Getter
public enum MapTypeEnum {
    /**
     * 腾讯地图
     */
    TENCENT(1, "腾讯地图"),
    
    /**
     * 高德地图
     */
    GAODE(2, "高德地图"),
    
    /**
     * 百度地图
     */
    BAIDU(3, "百度地图");
    
    /**
     * 类型代码
     */
    private final Integer code;
    
    /**
     * 类型名称
     */
    private final String name;
    
    MapTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据代码获取枚举
     * 
     * @param code 地图类型代码
     * @return 对应的枚举，默认返回腾讯地图
     */
    public static MapTypeEnum getByCode(Integer code) {
        if (code == null) {
            return TENCENT;
        }
        for (MapTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return TENCENT;
    }
} 