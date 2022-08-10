package com.easywecom.common.exception;

public class CallBackNullPointerException extends NullPointerException{
    private static final long serialVersionUID = 1L;

    public CallBackNullPointerException(String message) {
        super("user"+message);
    }
}
