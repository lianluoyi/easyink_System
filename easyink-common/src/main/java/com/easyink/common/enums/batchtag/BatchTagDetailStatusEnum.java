package com.easyink.common.enums.batchtag;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名: 批量打标签详情打标状态枚举
 *
 * @author : silver_chariot
 * @date : 2023/6/7 13:55
 **/
@Getter
@AllArgsConstructor
public enum BatchTagDetailStatusEnum {

    TO_BE_EXECUTED(0, "待执行"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");
    /**
     * 状态
     */
    private final Integer status;
    /**
     * 描述
     */
    private final String desc;
}
