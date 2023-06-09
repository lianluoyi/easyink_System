package com.easyink.common.enums;

/**
 * 类名：WeEmpleCodeAnalyseTypeEnum 员工活码数据分析type值定义
 *
 * @author Society my sister Li
 * @date 2021-11-08 21:42
 */
public enum WeEmpleCodeAnalyseTypeEnum {

    //新增
    ADD(1),
    //删除
    DEL(0);

    private final Integer type;

    WeEmpleCodeAnalyseTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

}
