package com.easyink.common.enums.moment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

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
    ALL_CUSTOMER(0, "全部客户"),
    /**
     * 选择部分客户
     */
    SELECT_CUSTOMER(1, "选择部分客户"),
    ;
    /**
     * 状态码
     */
    public final Integer type;
    /**
     * 含义
     */
    private final String desc;

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<MomentPushRangeEnum> getByCode(Integer code) {
        for (MomentPushRangeEnum value : values()) {
            if (value.type.equals(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * 校验or 返回
     *
     * @param code code
     * @return 枚举
     */
    public static MomentPushRangeEnum validCode(Integer code) {
        Optional<MomentPushRangeEnum> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("朋友圈推送类型异常"));
    }
}
