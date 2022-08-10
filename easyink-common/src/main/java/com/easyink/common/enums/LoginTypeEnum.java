package com.easyink.common.enums;

import com.easyink.common.core.domain.model.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型枚举
 *
 * @author : silver_chariot
 * @date : 2021/8/17 18:45
 */
@AllArgsConstructor
public enum LoginTypeEnum {
    /**
     * 账密登录
     */
    BY_PASSWORD(1,"PASSWORD_LOGIN"),
    /**
     * 内部扫码登录
     */
    BY_SCAN(2,"INTERNAL_SCAN_LOGIN"),
    /**
     * 网页登录
     */
    BY_WEB(3,"WEB_LOGIN"),
    /**
     * 三方扫码登录
     */
    BY_THIRD_SCAN(4,"THIRD_SCAN_LOGIN"),
    /**
     * 未知
     */
    UN_KNOWN(0,"UN_KNOWN");
    @Getter
    private final Integer type;

    @Getter
    private final String state;

    /**
     * 根据登录用户判断 是账密登录还是扫码登录
     *
     * @param loginUser 登录用户实体
     * @return
     */
    public static LoginTypeEnum getByUser(LoginUser loginUser) {
        if (loginUser == null) {
            return UN_KNOWN;
        }
        return loginUser.isSuperAdmin() ? BY_PASSWORD : BY_SCAN;
    }
}
