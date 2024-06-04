package com.easyink.common.exception.openapi;

/**
 * 类名: 校验签名异常
 *
 * @author : silver_chariot
 * @date : 2022/3/14 18:05
 */
public class SignValidateException extends OpenApiException {
    private static final long serialVersionUID = 1L;

    public SignValidateException(String message) {
        super(message);
    }
}
