package com.easyink.common.enums;

import com.easyink.common.enums.radar.RadarChannelEnum;
import lombok.Getter;

/**
 * 员工活码来源类型枚举值说明
 *
 * @author Society my sister Li
 * @date 2021/9/14
 */
@Getter
public enum EmployCodeSourceEnum {

    //活码创建
    CODE_CREATE(0, RadarChannelEnum.EMPLE_CODE),

    //新客建群
    NEW_GROUP(1, RadarChannelEnum.NEW_IN_GROUP),

    // 获客助手创建
    CUSTOMER_ASSISTANT(2, RadarChannelEnum.CUSTOMER_ASSISTANT),

    ;

    private final Integer source;
    private final RadarChannelEnum radarChannelEnum;

    EmployCodeSourceEnum(Integer source, RadarChannelEnum radarChannelEnum) {
        this.source = source;
        this.radarChannelEnum = radarChannelEnum;
    }

    /**
     * 根据source类型获取表单或雷达的对应渠道type
     *
     * @param source 活码来源类型
     * @return 表单/雷达对应渠道type
     */
    public static Integer getFromOrRadarType(Integer source) {
        if (source == null) {
            return RadarChannelEnum.UNKNOWN.getTYPE();
        }
        for (EmployCodeSourceEnum value : values()) {
            if (value.source.equals(source)) {
                return value.getRadarChannelEnum().getTYPE();
            }
        }
        return RadarChannelEnum.UNKNOWN.getTYPE();
    }
}
