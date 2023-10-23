package com.easyink.common.exception;

import com.dtflys.forest.exceptions.ForestRuntimeException;

/**
 * 重试异常类
 *
 * @author lichaoyu
 * @date 2023/9/27 11:39
 */
public class RetryException extends ForestRuntimeException {

    private static final long serialVersionUID = 1L;

    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryException(Throwable cause) {
        super(cause);
    }

}
