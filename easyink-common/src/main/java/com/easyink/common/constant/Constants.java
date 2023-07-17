package com.easyink.common.constant;

/**
 * 通用常量信息
 *
 * @author admin
 */
public class Constants {
    private Constants(){}
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";
    /**
     * 获取CODE失败
     */
    public static final String GET_CODE_FAIL =  "no.code";
    /**
     * 获取用户信息失败
     */
    public static final String GET_INFO_FAIL = "get.user.info.fail";

    /**
     * 企业未授权应用
     */
    public static final String CORP_NO_AUTH = "corp.no.auth";

    /**
     * 企业配置内部应用
     */
    public static final String CORP_NO_CONFIG_INTERNAL_APP = "corp.no.config.internal.app";

    /**
     * 非企业成员
     */
    public static final String NOT_IN_COMPANY = "not.in.company";

    public static final String USER_LOGIN_SUCCESS = "user.login.success";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens_new2:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 5;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";


    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = "sub";

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict_new2:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";


    /**
     * 是否为系统默认（是）
     */
    public static final int SERVICE_STATUS_ERROR = -1;


    /**
     * 启用状态
     */
    public static final String NORMAL_CODE = "0";

    /**
     * 删除状态(客户删除员工,流失客户)
     */
    public static final String DELETE_CODES = "1";

    /**
     * 授权未启用状态
     */
    public static final String NOT_START_CODE = "2";

    /**
     * 删除状态 (员工删除客户)
     */
    public static final String DELETE_CODE = "2";

    /**
     * 业务判断成功状态
     */
    public static final Integer SERVICE_RETURN_SUCCESS_CODE = 0;


    /**
     * 系统用户
     */
    public static final String USER_TYPE_SYS = "00";

    /**
     * 企业微信用户
     */
    public static final String USER_TYPE_WECOME = "11";


    /**
     * 企业管理
     */
    public static final String USER_TYOE_CORP_ADMIN = "22";


    /**
     * 企业微信用户系统中默认用户
     */
    public static final String DEFAULT_WECOME_ROLE_KEY = "WeCome";


    /**
     * 企业微信用户系统中默认用户
     */
    public static final String DEFAULT_WECOME_CORP_ADMIN = "CROP_ADMIN_ROLE";

    /**
     * 完成待办
     */
    public static final String HANDLE_SUCCESS = "3";
    /**
     * 扫码登录用户
     */
    public static final String QR_CODE_SCAN_USER = "qrCodeScanUser";
    /**
     * 网页登录用户
     */
    public static final String WEB_USER = "WebUser";

    /**
     * 所有权限标识
     */
    public static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    public static final String SUPER_ADMIN = "admin";

    /**
     * 初始化的菜单权限的菜单ID
     */
    protected static final Long[] INIT_MENU_LIST = new Long[]{2016L, 2014L, 2015L, 2022L, 2074L, 2020L, 2021L, 2002L, 2003L, 2001L, 2079L, 2080L, 2312L, 2313L, 2314L, 2315L, 2320L, 2327L, 2328L};

    /**
     * 获取初始化菜单权限的菜单ID
     *
     * @return INIT_MENU_LIST
     */
    public static Long[] getInitMenuList(){
        return INIT_MENU_LIST;
    }

    /**
     * 默认排序
     */
    public static final String DEFAULT_SORT = "1";
    /**
     * 缺省ID值
     */
    public static final Long DEFAULT_ID = -1L;
    /**
     * 系统默认后台主题颜色
     */
    public static final String DEFAULT_UI_COLOR = "#6BB4AB";
    /**
     * 回调处理锁的时间
     */
    public static final Long CALLBACK_HANDLE_LOCK_TIME = 3L;

    /**
     * 个人权限时，部门下有权限人数显示一人
     */
    public static final int ONE_NUM = 1;

    /**
     * CSV漏洞指攻击者利用特殊字符(“+、-、@、=”)
     */
    protected static final String[] CSV_INJECT_CHAR_LIST = {"+", "-", "@", "="};

    /**
     * 获得可能会引起CSV注入 的特殊字符序列
     * 
     * @return
     */
    public static String [] getCsvInjectCharList(){
        return CSV_INJECT_CHAR_LIST;
    }

    /**
     * TAB 空格字符
     */
    public static final String TAB = " ";
}
