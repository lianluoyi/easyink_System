package com.easywecom.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： MomentTaskTypeEnum
 *
 * @author 佚名
 * @date 2022/1/12 17:23
 */
@AllArgsConstructor
@Getter
public enum MomentTaskTypeEnum {
    /**
     * 立即发送
     */
    RIGHT_NOW(0, "立即发送"),
    /**
     * 定时发送
     */
    SETTING_TIME(1, "定时发送"),
    ;
    /**
     * 状态码
     */
    public final Integer type;
    /**
     * 含义
     */
    private final String desc;
}
