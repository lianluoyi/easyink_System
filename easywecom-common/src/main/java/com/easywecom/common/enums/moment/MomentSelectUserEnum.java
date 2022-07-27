package com.easywecom.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName： MomentSelectUserEnum
 *
 * @author wx
 * @date 2022/7/15 15:05
 */
@AllArgsConstructor
@Getter
public enum MomentSelectUserEnum {
    /**
     * 未选择员工
     */
    NOT_SELECT_USER(0, "未选择员工"),
    /**
     * 已选择员工
     */
    SELECT_USER(1, "已选择员工"),
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
