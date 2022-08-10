package com.easyink.common.enums.radar;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 类名: 雷达渠道枚举
 *
 * @author : silver_chariot
 * @date : 2022/7/19 10:45
 **/
@AllArgsConstructor
@Getter
public enum RadarChannelEnum {
    UNKNOWN(0, "未知渠道"),
    EMPLE_CODE(1, "员工活码"),
    MOMENT(2, "朋友圈"),
    GROUP_TASK(3, "客户群发"),
    SIDE_BAR(4, "侧边栏-雷达库"),
    WELCOME_MSG(5, "好友欢迎语"),
    CUSTOMER_SOP(6, "客户SOP"),
    GROUP_SOP(7, "群SOP"),
    NEW_IN_GROUP(8, "新客进群"),
    GROUP_CALENDAR(9, "群日历"),
    CUSTOMIZE(10, "自定义渠道");
    private final Integer TYPE;
    private final String NAME;

    /**
     * 根据类型获取系统默认的渠道
     *
     * @param type 类型
     * @return 渠道
     */
    public static String getChannelByType(int type) {

        RadarChannelEnum channel = Arrays.stream(values()).filter(a -> type == a.TYPE).findFirst().orElse(UNKNOWN);
        return channel.NAME;
    }

    /**
     * 判断是否是系统默认的渠道类型
     *
     * @param type 类型
     * @return true 是系统默认的渠道 false 不是
     */
    public static boolean isSysChannel(int type) {
        if (type == UNKNOWN.TYPE ||type == CUSTOMIZE.TYPE) {
            return false;
        }
        return true;
    }
}
