package com.easyink.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 代码生成通用常量
 *
 * @author admin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenConstants {

    /**
     * limit 1
     */
    public static final String LIMIT_1 = "LIMIT 1";
    public static final String LIMIT_5 = "LIMIT 5";

    /**
     * 计算概率拼接百分号
     */
    public static final String PERCENT = "%";

    /**
     * 正序
     */
    public static final String ASC = "ASC";

    /**
     * 倒序
     */
    public static final String DESC = "DESC";

    /**
     * 群聊名称占位符
     */
    public static final String GROUP_NAME = "${groupName}";
    /**
     * 客户昵称占位符
     */
    public static final String CUSTOMER = "${customer}";
    /**
     * 属性名称占位符
     */
    public static final String PROPERTY_NAME = "${propertyName}";
    /**
     * 员工名称占位符
     */
    public static final String USER_NAME = "${userName}";

    /**
     * 雷达标题占位符
     */
    public static final String RADAR_TITLE = "${radarTitle}";

    /**
     * 表单名称占位符
     */
    public static final String FORM_NAME = "${formName}";

    /**
     * 活动场景占位符
     */
    public static final String ACTIVITY_SCENE = "${activityScene}";

    /**
     * 活码名称占位符
     */
    public static final String CODE_NAME = "${codeName}";

    /**
     * 规则名称占位符
     */
    public static final String RULE_NAME = "${ruleName}";

    /**
     * 操作人占位符
     */
    public static final String OPERATOR = "${operator}";

    /**
     * 创建人占位符
     */
    public static final String CREATE_BY = "${createBy}";

    /**
     * 客户信息动态记录模板
     */
    public static final String EDIT_CUSTOMER_RECORD_MSG = "${userName}修改了${propertyName}为：";
    /**
     * 客户标签
     */
    public static final String CUSTOMER_TAG = "客户标签";


    /**
     * 文件类型 - txt
     */
    public static final String SUFFIX_TXT = "txt";
    /**
     * 网点ID
     */
    public static final String NETWORK_ID = "networkId";
}
