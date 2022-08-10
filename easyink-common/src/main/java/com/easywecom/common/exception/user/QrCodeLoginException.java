package com.easywecom.common.exception.user;

/**
 * 二维码扫码登录异常类
 *
 * @author silver_chariot
 */
public class QrCodeLoginException extends UserException {
    private static final long serialVersionUID = 1L;

    public QrCodeLoginException() {
        super("qrcode.login.error", null);
    }

    public QrCodeLoginException(String message) {
        super(message, null);
    }

}
