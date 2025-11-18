package com.easyink.wecom.domain.enums.mapapi;

import lombok.Getter;

import java.util.Optional;

/**
 * 地图api接口code枚举
 *
 * @author tigger
 * 2025/5/8 22:57
 **/
@Getter
public enum MapApiCodeEnum {
    /**
     * 获取省市区三级列表
     */
    DISTRICT_LIST(1),
    /**
     * 获取第四级行政区列表
     */
    DISTRICT_CHILDREN(2),
    /**
     * 地址解析
     */
    GEOCODER(3),
    /**
     * 逆地址解析
     */
    RE_GEOCODE(4),
    /**
     * 搜索建议
     */
    SUGGESTION(5),

    ;
    private final Integer code;

    MapApiCodeEnum(Integer code) {
        this.code = code;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<MapApiCodeEnum> getByCode(Integer code) {
        for (MapApiCodeEnum value : values()) {
            if (value.code.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 校验or 返回
     *
     * @param code code
     * @return 枚举
     */
    public static MapApiCodeEnum validCode(Integer code) {
        Optional<MapApiCodeEnum> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("地图api接口code枚举异常"));
    }
}
