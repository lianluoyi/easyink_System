package com.easyink.common.exception.user;

/**
 * 类名: 缺少登录后的TOKEN信息异常类
 *
 * @author : silver_chariot
 * @date : 2021/8/30 14:40
 */
public class NoLoginTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoLoginTokenException(String message) {
        super(message);
    }
}
