package com.easyink.wecom.domain.enums.form;

import lombok.Getter;

import java.util.Optional;

/**
 * 表单组件类型
 *
 * @author tigger
 * 2023/1/13 16:19
 **/
public enum FormComponentType {
    /**
     * 单选
     */
    RADIO_COMPONENT(1),
    /**
     * 多选
     */
    CHECKBOX_COMPONENT(2),
    /**
     * 下拉框
     */
    SELECT_COMPONENT(3),
    /**
     * 单行文本
     */
    ONE_LINE_TEXT_COMPONENT(4),
    /**
     * 多行文本
     */
    MANY_LINE_TEXT_COMPONENT(5),
    /**
     * 日期时间
     */
    DATE_TIME_COMPONENT(6),
    /**
     * 评分
     */
    SCORE_COMPONENT(7),
    /**
     * NPS
     */
    NPS_COMPONENT(8),
    /**
     * 文字
     */
    TEXT_COMPONENT(9),
    /**
     * 图片
     */
    IMAGE_COMPONENT(10),
    /**
     * 轮播图
     */
    CAROUSEL_COMPONENT(11),
    ;
    @Getter
    private final Integer code;

    FormComponentType(Integer code) {
        this.code = code;
    }

    /**
     * 根据类型获取枚举OP
     *
     * @param code 类型code
     * @return Optional
     */
    public static Optional<FormComponentType> getByCode(Integer code) {
        for (FormComponentType value : values()) {
            if (value.code.equals(code)) {
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
    public static FormComponentType validCode(Integer code) {
        Optional<FormComponentType> typeOp = getByCode(code);
        return typeOp.orElseThrow(() -> new RuntimeException("表单组件类型异常"));
    }

    /**
     * 是否是评分组件
     * 1. SCORE_COMPONENT
     * 2. NPS_COMPONENT
     *
     * @param code 组件类型
     * @return 评分组件
     */
    public static Boolean isGradeComponent(Integer code) {
        return FormComponentType.SCORE_COMPONENT.getCode().equals(code)
                || FormComponentType.NPS_COMPONENT.getCode().equals(code);
    }

}
