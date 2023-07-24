package com.easyink.wecom.openapi.exception;

/**
 * 类名: openApi异常
 *
 * @author : silver_chariot
 * @date : 2022/3/15 10:54
 */
public class OpenApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OpenApiException(String message) {
        super(message);
    }
}
