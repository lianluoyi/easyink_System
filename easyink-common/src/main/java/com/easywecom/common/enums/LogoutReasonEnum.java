package com.easywecom.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 类名: 登出原因枚举
 *
 * @author : silver_chariot
 * @date : 2021/10/21 21:42
 */
@AllArgsConstructor
public enum LogoutReasonEnum {
    /**
     * 未知原因
     */
    UN_KNOWN(0, ResultTip.TIP_GENERAL_UNAUTHORIZED),
    /**
     * 强制退出
     */
    FORCED(1, ResultTip.TIP_GENERAL_FORCE_LOGOUT),
    /**
     * 企业信息发生改变·
     */
    CORP_ID_CHANGED(2, ResultTip.TIP_GENERAL_CORP_ID_CHANGED),
    /**
     * 登录信息已过期
     */
    EXPIRED(3, ResultTip.TIP_GENERAL_UNAUTHORIZED);
    /**
     * 登出状态码
     */
    @Getter
    private final Integer code;
    /**
     * 应返回给前端的错误提示
     */
    @Getter
    private final ResultTip resultTip;

    /**
     * 根据CODE获取登出原因类型
     *
     * @param code 登出CODE
     * @return 登出类型枚举 不存在则返回UN_KNOWN
     */
    public static LogoutReasonEnum getByCode(Integer code) {
        if (null == code) {
            return UN_KNOWN;
        }
        return Arrays.stream(values()).filter(obj -> obj.getCode().equals(code)).findFirst().orElse(UN_KNOWN);
    }
}
