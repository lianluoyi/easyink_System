package com.easyink.wecom.domain.enums.form;

import com.easyink.common.enums.radar.RadarChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 表单渠道
 * code 与 {@link RadarChannelEnum} 统一
 *
 * @author wx
 * 2023/1/13 13:53
 **/
@AllArgsConstructor
public enum FormChannelEnum {
    UNKNOWN(0, "未知渠道"),
    SIDE_BAR(4, "侧边栏"),
    PROMOTION(11, "推广"),
    ;
    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    /**
     * 根据code获取表单渠道
     *
     * @param code  类型
     * @return  FormChannelEnum
     */
    public static FormChannelEnum getByCode(Integer code) {
        if(code == null) {
            return UNKNOWN;
        }
        return Arrays.stream(values()).filter(a -> code.equals(a.getCode())).findFirst().orElse(UNKNOWN);
    }
}
