package com.easyink.wecom.domain.enums.form;

import com.easyink.common.enums.EmployCodeSourceEnum;
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
    EMPLE_CODE(RadarChannelEnum.EMPLE_CODE.getTYPE(), RadarChannelEnum.EMPLE_CODE.getNAME()),
    NEW_IN_GROUP(RadarChannelEnum.NEW_IN_GROUP.getTYPE(), RadarChannelEnum.NEW_IN_GROUP.getNAME()),
    GROUP_TASK(RadarChannelEnum.GROUP_TASK.getTYPE(), "群发"),
    SIDE_BAR(RadarChannelEnum.SIDE_BAR.getTYPE(), "侧边栏"),
    WELCOME_MSG(RadarChannelEnum.WELCOME_MSG.getTYPE(), "欢迎语"),
    CUSTOMER_SOP(RadarChannelEnum.CUSTOMER_SOP.getTYPE(), RadarChannelEnum.CUSTOMER_SOP.getNAME()),
    GROUP_SOP(RadarChannelEnum.GROUP_SOP.getTYPE(), RadarChannelEnum.GROUP_TASK.getNAME()),
    GROUP_CALENDAR(RadarChannelEnum.GROUP_CALENDAR.getTYPE(), RadarChannelEnum.GROUP_CALENDAR.getNAME()),

    PROMOTION(11, "推广"),
    CUSTOMER_ASSISTANT(RadarChannelEnum.CUSTOMER_ASSISTANT.getTYPE(), RadarChannelEnum.CUSTOMER_ASSISTANT.getNAME())
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
