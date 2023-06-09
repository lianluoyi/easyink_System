package com.easyink.common.enums.radar;

import lombok.Getter;

import java.util.Arrays;

/**
 * ClassName： RadarTypeEnum 雷达类型
 *
 * @author wx
 * @date 2022/7/19 10:59
 */

public enum RadarTypeEnum {

    //企业
    CORP(3, "企业雷达库已更新“${radarTitle}"),
    //部门
    DEPARTMENT(2, "部门雷达库已更新“${radarTitle}”"),
    //个人
    SELF(1, ""),
    /**
     * 未知
     */
    UNKNOWN(0, ""),

    ;
    @Getter
    private final  Integer type;
    @Getter
    private final String updateNotice;

    RadarTypeEnum(Integer type, String updateNotice) {
        this.type = type;
        this.updateNotice = updateNotice;
    }

    public static RadarTypeEnum getByType(Integer type) {
        if (type == null) {
            return UNKNOWN;
        }
        return Arrays.stream(values()).filter(a -> type.equals(a.getType())).findFirst().orElse(UNKNOWN);
    }


}
