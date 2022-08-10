package com.easyink.common.exception;

import com.easyink.common.enums.ResultTip;

import java.text.MessageFormat;

/**
 * 自定义异常
 *
 * @author admin
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private final String message;

    public CustomException(String message) {
        this.message = message;
    }

    public CustomException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public CustomException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    /**
     * 直接使用ResultTip构造
     *
     * @param resultTip ${@link ResultTip}
     */
    public CustomException(ResultTip resultTip) {
        this.message = resultTip.getTipMsg();
        this.code = resultTip.getCode();
    }

    /**
     * 直接使用ResultTip构造,msgParam替换占位符{0}、{1}
     *
     * @param resultTip ${@link ResultTip}
     */
    public CustomException(ResultTip resultTip, String msg, String... msgParam) {
        this.message = MessageFormat.format(msg, msgParam);
        this.code = resultTip.getCode();
    }

    /**
     * 提供自定义返回消息的构造方法
     *
     * @param resultTip ${@link ResultTip}
     * @param msg       自定义消息
     */
    public CustomException(ResultTip resultTip, String msg) {
        this.message = msg;
        this.code = resultTip.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
