package com.easyink.wecom.openapi.exception;

/**
 * 类名: App_id生成异常
 *
 * @author : silver_chariot
 * @date : 2022/3/14 11:31
 */
public class AppGenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AppGenException(String message) {
        super(message);
    }
}
