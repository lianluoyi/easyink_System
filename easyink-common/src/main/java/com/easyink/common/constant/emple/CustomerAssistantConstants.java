package com.easyink.common.constant.emple;

import java.util.stream.Stream;

/**
 * 获客助手常量类
 *
 * @author lichaoyu
 * @date 2023/8/22 14:51
 */
public class CustomerAssistantConstants {
    /**
     * 获客链接截取state值的标志
     */
    public static final String STATE_SPLIT = "=";

    /**
     * 获客链接的渠道state前缀
     */
    public static final String STATE_PREFIX = "hk_";

    /**
     * 获客链接url后拼接的的state参数名称
     */
    public static final String STATE_URL = "?customer_channel=";

    /**
     * 默认渠道名称
     */
    public static final String DEFAULT_CHANNEL_NAME = "默认渠道";

    /**
     * 留存率为空时返回的值
     */
    public static final String NULL_VALUE = "-";

    /**
     * 导出客户维度报表名称
     */
    public static final String EXPORT_CUSTOMER_NAME = "的统计报表(客户维度)";

    /**
     * 导出渠道维度报表名称
     */
    public static final String EXPORT_CHANNEL_NAME = "的统计报表(渠道维度)";

    /**
     * 导出日期维度报表名称
     */
    public static final String EXPORT_DATE_NAME = "的统计报表(日期维度)";

    /**
     * 告警通知类型：每天仅通知一次
     */
    public static final Integer ONLY_ONE_WARN_TYPE = 0;

    /**
     * 告警通知类型：每次都告警
     */
    public static final Integer EVERY_WARN_TYPE = 1;

    /**
     * 链接名称占位符
     */
    public static final String CUSTOMER_ASSISTANT_LINK_NAME = "{链接名称}";

    /**
     * 即将过期的额度数占位符
     */
    public static final String EXPIRE_QUOTA_NUM = "{即将过期的额度数}";

    /**
     * 过期时间占位符
     */
    public static final String EXPIRE_TIME = "{过期时间}";

    /**
     * 购买获客助手额度Link文案
     */
    public static final String BUY_LINK_TEXT = "<a href=\"https://work.weixin.qq.com/wework_admin/loginpage_wx?etype=noTtl&redirect_uri=https%3A%2F%2Fwork.weixin.qq.com%2Fwework_admin%2Fframe#/customer/customerAcquisition/buy\">前往购买更多使用量></a>";


    /**
     * 获客助手不可用通知文案
     */
    public static final String LINK_UNAVAILABLE_NOTICE = "【获客助手】" + "\n" +
            "提醒内容：获客链接"+ "\"" + CUSTOMER_ASSISTANT_LINK_NAME + "\"" +"暂时无法添加客户，请及时回收已投放的链接";

    /**
     * 获客助手使用量即将耗尽通知文案
     */
    public static final String BALANCE_LOW_NOTICE = "【获客助手】" + "\n" +
            "提醒内容：获客助手的使用量即将耗尽，为保证引流效果，请及时续费" + "\n" + BUY_LINK_TEXT;

    /**
     * 获客助手额度已耗尽通知文案
     */
    public static final String BALANCE_EXHAUSTED_NOTICE = "【获客助手】" + "\n" +
            "提醒内容：获客助手的使用量已耗尽，客户无法通过获客链接添加员工，请及时回收已投放的链接" + "\n" + BUY_LINK_TEXT;

    /**
     * 获客额度即将过期通知文案
     */
    public static final String QUOTA_EXPIRE_SOON_NOTICE = "【获客助手】" + "\n" +
            "提醒内容：您有" + EXPIRE_QUOTA_NUM  +"个获客额度数在" + EXPIRE_TIME + "后过期，官方规定，额度数过期后将自动扣除，请及时投放，避免浪费";

    /**
     * 新增获客链接接口最大的关联员工数量，详见API：( https://developer.work.weixin.qq.com/document/path/97297#%E5%88%9B%E5%BB%BA%E8%8E%B7%E5%AE%A2%E9%93%BE%E6%8E%A5 )
     */
    public static final Integer MAX_LINK_USER_NUMS = 100;

}
