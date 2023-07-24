package com.easyink.wecom.openapi.constant;

/**
 * 类名: 第三方开发参数相关常量类
 *
 * @author : silver_chariot
 * @date : 2022/3/14 15:47
 */
public class AppInfoConst {
    public AppInfoConst() {
    }

    /**
     * 请求头的key ： app_id
     */
    public static final String HEADER_APP_ID = "app-id";

    /**
     * app_secret
     */
    public static final String APP_SECRET = "app-secret";
    /**
     * 请求头的key ： 签名
     */
    public static final String HEADER_SIGN = "sign";
    /**
     * 请求头的key ： 18位随机数
     */
    public static final String HEADER_NONCE = "nonce";
    /**
     * 请求头的key ： 时间戳
     */
    public static final String HEADER_TIME_STAMP = "timestamp";
    /**
     * 请求头的key ： ticket
     */
    public static final String HEADER_TICKET = "ticket";


    /**
     * 请求过期时间
     */
    public static final long REQ_EXPIRE_TIME = 60L * 5L;

    /**
     * 票据过期时间
     */
    public static final int TICKET_EXPIRE_TIME = 2 * 60 * 60;
    /**
     * 随机字符串的长度
     */
    public static final int NONCE_LENGTH = 18;

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;
    /**
     * 失败状态码
     */
    public static final int FAIL_CODE = 1;
    /**
     * 成功默认消息
     */
    public static final String SUCCESS_MSG = "success";
}
