package com.easywecom.common.enums.autotag;

import lombok.Getter;

/**
 * 自动标签匹配类型枚举
 *
 * @author tigger
 * 2022/2/27 18:51
 **/
public enum AutoTagMatchTypeEnum {
    /**
     * 模糊匹配
     */
    FUZZY(1) {
        @Override
        public boolean match(String originText, String matchText) {
            return originText.contains(matchText);
        }
    },
    /**
     * 精确匹配
     */
    EXACT(2) {
        @Override
        public boolean match(String originText, String matchText) {
            return originText.equals(matchText);
        }
    },
    ;

    @Getter
    private Integer type;

    AutoTagMatchTypeEnum(Integer type) {
        this.type = type;
    }


    public static AutoTagMatchTypeEnum getByType(Integer type) {
        for (AutoTagMatchTypeEnum value : AutoTagMatchTypeEnum.values()) {

            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public abstract boolean match(String originText, String matchText);
}
