package com.easyink.common.enums.wemsgtlp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 过滤条件关联逻辑判断枚举
 *
 * @author lichaoyu
 * @date 2023/10/26 9:22
 */
@AllArgsConstructor
@Getter
public enum LogicTypeEnum {

    /**
     * 逻辑判断 或
     */
    OR(0),
    /**
     * 逻辑判断 且
     */
    AND(1);

    /**
     * 判断类型
     */
    private final Integer type;

}
