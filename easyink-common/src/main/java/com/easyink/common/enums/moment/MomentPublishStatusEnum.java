package com.easyink.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： MomentPublishStatusEnum
 *
 * @author 佚名
 * @date 2022/1/12 10:36
 */
@AllArgsConstructor
@Getter
public enum MomentPublishStatusEnum {
    /**
     * 待发布
     */
    NOT_PUBLISH(0, "待发布"),
    /**
     * 已发布
     */
    PUBLISH(1, "已发布"),
    /**
     * 已过期
     */
    EXPIRE(2, "已过期"),

    NO_AUTHORITY(3, "不可发布"),
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
