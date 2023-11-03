package com.easyink.common.enums.wemsgtlp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 欢迎语客户筛选类型枚举
 *
 * @author lichaoyu
 * @date 2023/10/25 18:02
 */
@AllArgsConstructor
@Getter
public enum WeMsgTlpFilterEnum {

    /**
     * 来源
     */
    SOURCE(0, "来源"),
    /**
     * 性别
     */
    GENDER(1, "性别");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String name;

}
