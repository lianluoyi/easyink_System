package com.easyink.common.enums;

/**
 * 方法参数类型（实体 or 基本数据类型）
 *
 * @author wx
 * 2023/3/14 16:15
 **/
public enum MethodParamType {

    /**
     * 实体
     */
    STRUCT,
    /**
     * 基本数据类型 包括String和基本数据类型包装类
     */
    BASE;
}
