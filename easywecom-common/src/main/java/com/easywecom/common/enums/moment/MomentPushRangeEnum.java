package com.easywecom.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： MomentPushRangeEnum
 *
 * @author 佚名
 * @date 2022/1/16 13:54
 */
@AllArgsConstructor
@Getter
public enum MomentPushRangeEnum {
    /**
     * 全部客户
     */
    ALL_CUSTOMER(0,"全部客户"),
    /**
     * 选择部分客户
     */
    SELECT_CUSTOMER(1, "选择部分客户"),;
    /**
     * 状态码
     */
    public final Integer type;
    /**
     * 含义
     */
    private final String desc;
}
