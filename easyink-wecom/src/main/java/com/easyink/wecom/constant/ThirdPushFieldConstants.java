package com.easyink.wecom.constant;

/**
 * 第三方推送字段常量
 *
 * @author easyink
 */
public class ThirdPushFieldConstants {

    /**
     * 手机号字段默认值
     */
    public static final String DEFAULT_DEVICE_1 = "DEVICE_1";

    /**
     * 客户昵称默认字段
     */
    public static final String DEFAULT_CUSTOMER_FIELD_2 = "CustomerField2";

    /**
     * 安装地址默认字段
     */
    public static final String DEFAULT_CUSTOMER_FIELD_3 = "CustomerField3";

    /**
     * 所属外包单位字段
     */
    public static final String CUSTOMER_FIELD_4 = "CustomerField4";

    /**
     * 上门员工号字段
     */
    public static final String CUSTOMER_FIELD_5 = "CustomerField5";

    /**
     * 是否满意字段 （1：非常满意；2：满意；3：不满意）—— 超时推送时无需传
     */
    public static final String CUSTOMER_FIELD_6 = "CustomerField6";

    /**
     * 服务不满意内容字段
     */
    public static final String CUSTOMER_FIELD_7 = "CustomerField7";

    /**
     * 服务建议内容字段
     */
    public static final String CUSTOMER_FIELD_8 = "CustomerField8";

    /**
     * 交互模式固定值
     */
    public static final String INTERACTIVE_MODE = "OB";

    /**
     * 项目名称固定值
     */
    public static final String PROJECT_NAME = "超高清升级项目企微用户满意度";

    /**
     * 答案最大推送长度限制
     */
    public static final int MAX_ANSWER_LENGTH = 200;

    private ThirdPushFieldConstants() {
        // 工具类，禁止实例化
    }
}
