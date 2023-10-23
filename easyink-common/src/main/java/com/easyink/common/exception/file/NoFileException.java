package com.easyink.common.exception.file;

/**
 * 类名: 文件找不到异常
 *
 * @author : silver_chariot
 * @date : 2023/9/22 15:34
 **/
public class NoFileException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NoFileException(String message) {
        super(message);
    }
}
