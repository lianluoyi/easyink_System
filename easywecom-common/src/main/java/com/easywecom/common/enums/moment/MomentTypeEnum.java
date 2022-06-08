package com.easywecom.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名： 朋友圈类型
 *
 * @author 佚名
 * @date 2022/1/10 16:11
 */
@AllArgsConstructor
@Getter
public enum MomentTypeEnum {
    /**
     * 企业朋友圈
     */
    ENTERPRISE_MOMENT(0, "企业朋友圈"),
    /**
     * 个人朋友圈
     */
    PERSONAL_MOMENT(1, "个人朋友圈"),
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
