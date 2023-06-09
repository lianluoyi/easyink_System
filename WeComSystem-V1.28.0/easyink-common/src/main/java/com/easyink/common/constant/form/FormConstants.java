package com.easyink.common.constant.form;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.utils.DateUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;


/**
 * 智能表单常量类
 *
 * @author wx
 * 2023/1/15 9:12
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormConstants {


    /**
     * 企业初始化默认分组名称
     */
    public static final String DEFAULT_CORP_FORM_GROUP_NAME = "默认分组";
    /*
     * 表单链接（长链）中域名占位符
     */
    public static final String DOMAIN_PLACEHOLDER = "{domain}";

    /**
     * 表单链接（长链）中formId占位符
     */
    public static final String FORM_ID_PLACEHOLDER = "{formId}";

    /**
     * 表单链接（长链）中userId占位符
     */
    public static final String USER_ID_PLACEHOLDER = "{userId}";

    /**
     * 表单链接（长链）中channelType占位符
     */
    public static final String CHANNEL_TYPE_PLACEHOLDER = "{channleType}";

    /**
     * 表单长链
     */
    public static final String FORM_URL = DOMAIN_PLACEHOLDER + "?"
            + "formId=" + FORM_ID_PLACEHOLDER
            + "&" + "userId=" + USER_ID_PLACEHOLDER
            + "&" + "channelType=" + CHANNEL_TYPE_PLACEHOLDER;



    /**
     * 生成表单链接
     *
     * @param domain        中间页域名
     * @param formId        表单id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    public static String genFormUrl(String domain, Long formId, String userId,Integer channelType) {
        if (formId == null || channelType == null || StringUtils.isAnyBlank(domain, userId)) {
            return null;
        }
        return FORM_URL.replace(DOMAIN_PLACEHOLDER, domain)
                .replace(FORM_ID_PLACEHOLDER, String.valueOf(formId))
                .replace(USER_ID_PLACEHOLDER, userId)
                .replace(CHANNEL_TYPE_PLACEHOLDER, String.valueOf(channelType));
    }

    /**
     * 生成完整的短链
     *
     * @param domain 域名
     * @param code   短链后缀的字符串
     * @return 完整的短链
     */
    public static String genShortUrl(String domain, String code) {
        return domain + WeConstans.SLASH + code;
    }

    /**
     * 表单名称占位符
     */
    private static final String FORM_TITLE_PLACEHOLDER = "{表单名称}";

    /**
     * 客户昵称占位符
     */
    private static final String CUSTOMER_NICKNAME_PLACEHOLDER = "{客户昵称}";

    /**
     * 时间占位符
     */
    private static final String TIME_PLACEHOLDER = "{时刻}";

    /**
     * 提交表单行为通知
     */
    public static final String COMMIT_FORM_NOTICE = "【智能表单】" + "\n" +
         "表单名称：" + FORM_TITLE_PLACEHOLDER + "\n" +
         "提醒内容：客户" + CUSTOMER_NICKNAME_PLACEHOLDER + "于" + TIME_PLACEHOLDER + "提交表单";

    /**
     * 获取客户提交表单行为通知内容
     *
     * @param title             表单名称
     * @param customerNickName  客户昵称
     * @param date              提交时刻
     * @return
     */
    public static String getCommitNoticeContent(String title, String customerNickName, Date date) {
        return COMMIT_FORM_NOTICE.replace(FORM_TITLE_PLACEHOLDER, StringUtils.defaultString(title))
                .replace(CUSTOMER_NICKNAME_PLACEHOLDER, StringUtils.defaultString(customerNickName))
                .replace(TIME_PLACEHOLDER, date == null ? StringUtils.EMPTY : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, date));
    }


    /**
     * 前端dashboard域名占位符
     */
    public static final String DASHBOARD_DOMAIN = "{dashboardDomain}";

    /**
     * 授权成功后由后端重定向前端的地址
     */
    public static final String ALTER_AUTH_REDIRECT_URL = "http://" + DASHBOARD_DOMAIN + "/#/system/configCenter/offAccount";

    /**
     * 获取授权成功后由后端重定向前端的地址
     *
     * @param dashboardDomain   前端dashboard域名
     * @return
     */
    public static String getAlterAuthRedirectUrl(String dashboardDomain) {
        return ALTER_AUTH_REDIRECT_URL.replace(DASHBOARD_DOMAIN, StringUtils.defaultString(dashboardDomain));
    }

}
