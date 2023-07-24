package com.easyink.common.enums.callback;

import com.easyink.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 类名: 企微回调事件枚举
 *
 * @author : silver_chariot
 * @date : 2023/7/14 10:29
 **/
@Getter
@AllArgsConstructor
public enum WecomCallbackEventEnum {
    UNKNOWN("unknown"),
    /**
     * 新增客户
     */
    ADD_EXTERNAL_CONTACT("add_external_contact"),
    /**
     * 流失客户
     */
    DEL_FOLLOW_USER("del_follow_user"),
    /**
     * 编辑客户
     */
    EDIT_EXTERNAL_CONTACT("edit_external_contact"),
    ;


    /**
     * 类型
     */
    private String type;

    /**
     * 根据类型返回枚举
     *
     * @param type 回调类型
     * @return {@link  WecomCallbackEventEnum}
     */

    public static WecomCallbackEventEnum getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return UNKNOWN;
        }
        return Arrays.stream(values()).filter(a -> type.equals(a.getType())).findFirst().orElse(UNKNOWN);
    }
    }
