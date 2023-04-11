package com.easyink.common.constant.miniapp;

import java.net.URLEncoder;

/**
 * 类名: 小程序常量
 *
 * @author : silver_chariot
 * @date : 2022/8/23 14:00
 **/
public class MiniAppConst {

    /**
     * 员工活码页面路径
     */
    public static final String USER_CODE_PAGE_PATH = "pages/code/employeesCode";
    /**
     * 群活码页面路径
     */
    public static final String GROUP_CODE_PAGE_PATH = "pages/code/groupCode";
    /**
     * 活码小程序 员工活码页面queryString
     */
    public static final String USER_CODE_QUERY_STRING = "type=${TYPE}&qrcodeUrl=${CODE_URL}";
    /**
     * 活码小程序 群活码 页面query string
     */
    public static final String GROUP_CODE_QUERY_STRING = "type=${TYPE}&actualQRCode=${CODE_URL}&activityName=${ACT_NAME}&tipMsg=${TIP_MSG}&guide=${GUIDE}&isOpenTip=${IS_OPEN_TIP}&serviceQrCode=${SERVICE_QR_CODE}&groupName=${GROUP_NAME}";
    /**
     * type 占位符
     */
    public static final String TYPE_SUB = "${TYPE}";
    /**
     * id占位符
     */
    public static final String ID_SUB = "${ID}";
    /**
     * 活码链接占位符
     */
    public static final String CODE_URL = "${CODE_URL}";
    /**
     * 活动名 占位符
     */
    public static final String ACT_NAME = "${ACT_NAME}";
    /**
     * 进群提示语 占位符
     */
    public static final String TIP_MSG = "${TIP_MSG}";
    /**
     * 引导语 占位符
     */
    public static final String GUIDE = "${GUIDE}";
    /**
     * 客服二维码占位符
     */
    public static final String SERVICE_QR_CODE = "${SERVICE_QR_CODE}";
    /**
     * 是否开启提示占位符
     */
    public static final String IS_OPEN_TIP = "${IS_OPEN_TIP}";
    /**
     * 群聊名称占位符
     */
    public static final String GROUP_NAME = "${GROUP_NAME}";

    private MiniAppConst() {
    }

    /**
     * 生成员工活码小程序queryString
     *
     * @param type      短链类型{@link com.easyink.common.shorturl.enums.ShortUrlTypeEnum }
     * @param qrcodeUrl 活码id
     * @return querystring for mini app
     */
    public static String genEmpleCodeQueryString(Integer type, String qrcodeUrl) {
        return USER_CODE_QUERY_STRING.replace(TYPE_SUB, String.valueOf(type))
                                     .replace(CODE_URL, URLEncoder.encode(qrcodeUrl));
    }


}
