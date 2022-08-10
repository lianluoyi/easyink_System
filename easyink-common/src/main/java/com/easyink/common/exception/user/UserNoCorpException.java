package com.easyink.common.exception.user;

/**
 * 类名: UserNoCorpException
 * 若获取不到缓存中用户的公司ID,则抛出此异常
 *
 * @author : silver_chariot
 * @date : 2021/8/30 14:40
 */
public class UserNoCorpException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNoCorpException(String message) {
        super(message);
    }
}
