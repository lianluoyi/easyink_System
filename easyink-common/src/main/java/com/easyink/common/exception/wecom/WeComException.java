package com.easyink.common.exception.wecom;

import com.easyink.common.enums.WeExceptionTip;

/**
 * @description: 企业微信相关异常类
 * @author admin
 * @create: 2020-08-26 16:52
 **/
public class WeComException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected String message;

    private Integer code;

    public WeComException(String message) {
        this.message = message;
    }

    public WeComException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取异常提示语信息
     *
     * @param code 企微返回的errcode
     */
    public WeComException(Integer code) {
        this.code = code;
        this.message = WeExceptionTip.getTipMsg(code);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
