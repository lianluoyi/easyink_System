package com.easywecom.common.enums;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

/**
 * 类名: ResultTip
 *
 * @author: 1*+
 * @date: 2021-09-26 10:15
 */
public enum ResultTip {

    /**
     * edit by 1*+
     * 基础错误码: 1xxx
     * 运营中心错误码: 2xxx
     * 员工模块错误码: 3XXX
     * 客户模块错误码: 4xxx
     * 会话存档错误码: 5xxx
     * 报表模块错误码: 6xxx
     * 三方错误码： 7xxx
     * 应用中心错误码：8xxx
     */
    //通用返回码
    TIP_GENERAL_SUCCESS(200, "操作成功"),
    TIP_GENERAL_BAD_REQUEST(400, "服务异常，请联系客服或技术人员"),
    TIP_GENERAL_UNAUTHORIZED(401, "没有操作权限，请在合规渠道使用系统"),
    TIP_GENERAL_FORBIDDEN(403, "没有操作权限，请联系管理员"),
    TIP_GENERAL_NOT_FOUND(404, "资源，服务未找到!"),
    TIP_GENERAL_FORCE_LOGOUT(405, "你已被管理员强制退出，请重新登录或联系管理员"),
    TIP_GENERAL_CORP_ID_CHANGED(406, "企业信息变更，为保证系统正常使用，请重新登录"),
    TIP_GENERAL_ERROR(500, "服务异常，请联系客服或技术人员"),

    //系统自定义业务返回码
    TIP_ATTRIBUTED(501, "有员工在使用该角色，不可删除"),
    TIP_NOT_AVAILABLE_CONFIG_FOUND(503, "没有可用的企业微信配置，请联系管理员"),
    TIP_MISS_CORP_ID(600, "获取企业信息异常，请重新登录或联系管理员配置企业ID"),
    TIP_MISS_APPID(601, "获取应用信息异常，请重新登录或联系管理员配置应用ID"),
    TIP_MISS_PROVIDER_CONFIG(602, "没有找到企业服务商配置"),
    TIP_MISS_SUITE_TICKET(603, "缺失服务商Ticket,请检查缓存或者在服务商后台重新获取"),
    TIP_NOT_CONFIG_CONTACT(1000, "所在企业未配置通讯录，请联系企业管理员登录系统并完善配置!"),
    TIP_NOT_AUTH_CORP(1001, "所在企业未授权当前应用，请联系企业管理员进行授权"),
    TIP_SERVER_NOT_SUPPORT(1002, "当前环境不支持通过该二维码登录，请联系客服或技术人员"),
    TIP_SERVER_NOT_SUPPORT_INTERFACE(1003, "当前环境不支持该请求，请联系客服或技术人员"),
    TIP_CORP_MISMATCH(1004, "登录企业与原有企业不一致"),
    TIP_TIME_FORMAT_ERROR(1005, "时间格式错误！"),
    TIP_FAIL_GET_JOIN_CORP_QRCODE(1006, "获取加入企业二维码失败"),
    TIP_ROLE_NOT_EXIST(1007, "角色不存在"),
    TIP_DEPARTMENT_NOT_EXIST(1008, "部门不存在"),
    TIP_TIME_RANGE_FORMAT_ERROR(1009, "时间段范围填写错误！"),
    TIP_DOWNLOAD_ERROR(1010, "下载异常！"),
    TIP_NO_DATA_TO_EXPORT(1011, "没有可导出的数据"),
    TIP_START_AFTER_END_TIME(1012, "开始时间不能大于结束时间！"),
    TIP_NOT_CORP_CONFIG(1013, "当前企业未完成企微配置，无法使用该功能"),
    TIP_GENERAL_RESET_TIME(1014, "填写时间过于接近当前，请重新调整时间！"),
    TIP_STRATEGY_IS_EMPTY(1015, "未找到对应的策略处理器"),
    TIP_MISS_SUITE_ID_HEADER(1016, "没有增加suiteId请求头"),
    TIP_NOT_START_APP_CONFIG(1017, "当前企业已授权但未启用待开发应用，无法使用该功能"),
    TIP_NOT_AUTH_DK_CORP(1018, "所在企业未授权当前代开发应用，请联系企业管理员进行授权"),
    TIP_ILLEGAL_DOMAIN(1019, "填写的服务器信息域名主体与当前企业不一致"),
    TIP_NO_CAPTCHA_OR_TLKEY(1020, "短信验证码或者tlKey为空"),
    TIP_ERROR_CAPTCHA(1021, "短信验证码错误"),
    TIP_CONFIRM_CAPTCHA(1022, "验证短信失败"),


    //运营模块返回码
    TIP_ATTACHMENT_OVER(2001, "附件数量已达上限，请重新选择"),
    TIP_SEND_MESSAGE_ERROR(2002, "请填写发送内容"),
    TIP_NO_CUSTOMER(2003, "所选员工没有可以群发的客户，请重新选择"),
    TIP_MATERIAL_COVER(2004, "素材标签不能重复，请重新设置"),
    TIP_TIME_TASK_LESS_CURR(2005, "定时发送时间不能小于当前时间"),
    TIP_EXPIRETIME_LESS_CURR(2006, "过期时间不能小于当前时间！"),
    TIP_EXPIRETIME_DATA_ERROR(2007, "过期时间格式错误！"),
    TIP_MISS_MATERIAL_ID(2008, "服务异常，请联系客服或技术人员"),
    TIP_MISS_MATERIAL_TAG_PARAMETER(2009, "操作失败，请联系客服或技术人员"),
    TIP_CHECK_STAFF(2010, "请选择使用人员"),
    TIP_NO_GROUP(2011, "所选员工没有可以群发的客户群，请重新选择"),
    TIP_PARENT_NOT_LEVEL1_CATEGORY(2012, "上级分组名非一级分组名"),
    TIP_WORDS_CATEGORY_DUPLICATION_NAME(2013, "分组名重名"),
    TIP_ROOT_NODE_CANNOT_DELETE(2014, "不能删除根节点数据！"),
    TIP_ROOT_NODE_UN_FIND(2015, "查无根节点数据！"),
    TIP_WORDS_CATEGORY_SAME_NAME(2016, "已存在！"),
    TIP_GENERAL_PARAM_ERROR(2017, "参数值填写错误"),
    TIP_ADMIN_NOT_CREATE_DEPART_WORDS_CATEGORY(2018, "管理员不能创建部门话术分组名"),
    TIP_PARENT_WORDS_CATEGORY_NOT_EXIST(2019, "上级分组名不存在"),
    TIP_MISS_WORDS_PARAMETER(2020, "话术库缺少必要参数，保存更新失败，请检查参数"),
    TIP_MISS_WORDS_GROUP_ID(2021, "话术库缺少话术ID，删除失败，请检查参数"),
    TIP_MISS_WORDS_CATEGORY_ID(2022, "话术库缺少话术分组名ID，请检查参数"),
    TIP_MISS_WORDS_ATTACHMENT_CONTENT(2023, "话术库附件缺少内容，保存更新失败，请检查参数"),
    TIP_ACTIVE_CODE_NOT_EXSIT(2024, "所选群活码失效，请重新选择"),
    TIP_TASK_NAME_EXSIT(2025, "任务已存在，请重新填写任务名"),
    TIP_NOT_SELECT_STAFF(2026, "未选择使用员工，请重新选择"),
    TIP_MESSAGE_TASK_DELETE_ERROR(2027, "群发任务删除失败，只能删除未执行的任务"),
    TIP_MESSAGE_TASK_UPDATE_ERROR(2028, "群发任务更新失败，只能更新未执行的任务"),
    TIP_MESSAGE_TO_LONG_ERROR(2029, "创建失败，文字内容过长"),

    TIP_GROUP_CODE_NAME_OCCUPIED(2030, "客户群活码名称已存在，添加群活码失败！"),
    TIP_ACTUAL_GROUP_CODE_EXIST(2031, "新增实际群码失败，该群聊二维码已重复存在"),
    TIP_ACTUAL_GROUP_OVER_NUM_TWO_HUNDRED(2032, "实际群码扫码入群人数超过上限200人"),
    TIP_MISS_ACTUAL_GROUP(2033, "实际群码未选择客户群"),
    TIP_WORDS_OVER_TITLE(2034, "话术附件标题长度超出限制"),
    TIP_URL_CONTENT_ERROR(2035, "未获取到链接信息，请自定义链接信息"),
    TIP_MISS_WORDS_SORT_INFO(2036, "话术库缺少排序号、话术id,修改失败"),
    TIP_LAST_USE_OVER_NUM(2037, "话术库最近使用不能保存超过5个"),
    TIP_EMPLY_CODE_NOT_FOUND(2038, "员工活码数据未找到"),
    TIP_NO_AVAILABLE_GROUP_CODE(2039, "没有可用的客户群"),
    TIP_ADD_CUSTOMER_SOP_ERROR(2040, "添加客户sop失败，缺少员工id"),
    TIP_SENDING_MESSAGE(2041, "正在派送群发任务，请稍后再试"),
    TIP_GROUP_CALENDAR_ADD_PARAMETER_ERROR(2042, "群SOP添加失败，使用群聊、日历名称或任务规则为空"),
    TIP_PLEASE_INPUT_DEFAULT_MSG(2043, "请填写默认欢迎语"),
    TIP_PLEASE_INPUT_SPECIAL_MSG_TIME(2044, "请选择特殊时段时间"),
    TIP_DEFAULT_MSG_TOO_LONG(2045, "欢迎语长度超过限制"),
    TIP_MSG_TYPE_NOT_FUND(2046, "欢迎语模板类型未找到"),
    TIP_MSG_REPEATED_TIME(2047, "欢迎语时段重叠"),
    TIP_NOT_FIND_ATTACHMENT_TYPE(2048, "未找到欢迎语对应素材类型"),
    TIP_MOMENT_ATTACHMENT_TYPE_ERROR(2049, "创建朋友圈任务失败，附件类型不合法只允许图片、链接、视频中的一种"),
    TIP_MOMENT_ATTACHMENT_NUM_ERROR(2050, "创建朋友圈任务失败，附件数量不合法"),
    TIP_MOMENT_ATTACHMENT_CONTENT_ERROR(2051, "创建朋友圈任务失败，发布内容不能为空"),
    TIP_MOMENT_ATTACHMENT_SEND_TIME_ERROR(2052, "创建朋友圈任务失败，定时发布时间不能为空"),
    TIP_MOMENT_UPDATE_ERROR(2053, "更新朋友圈失败，只有定时未发送的朋友圈可编辑"),
    TIP_MOMENT_CREATE_ERROR(2054, "所选可见范围下没有微信客户，无法创建朋友圈"),
    TIP_MOMENT_ATTACHMENT_CREATE_ERROR(2055, "朋友圈素材超出限定大小，请重新选择"),
    TIP_ACTUAL_GROUP_OVER_NUM_ONE_THOUSAND(2056, "实际群码扫码入群人数超过上限1000人"),
    TIP_ACTUAL_GROUP_REF_GROUP_LIMIT_SIZE(2057, "每个企业微信活码最多关联5个群聊"),
    TIP_URL_ERROR(2058, "请填写http或https开头的合法链接地址"),
    TIP_URL_UNKNOWN_HOST(2059, "链接地址域名无法访问"),
    TIP_URL_MATCH_ERROR(2060, "获取链接默认信息失败"),
    TIP_REDEEM_CODE_INPUT_TIME_ERROR(2061, "未填写时间"),
    TIP_REDEEM_CODE_EMPTY_THRESHOLD(2062, "阈值不能为负值"),
    TIP_REDEEM_CODE_EMPTY_USERS(2063, "未选择员工"),
    TIP_REDEEM_CODE_END_TIME_GE_START_TIME(2064, "结束时间应大于开始时间"),
    TIP_REDEEM_CODE_REPEAT(2065, "兑换码重复"),
    TIP_REDEEM_CODE_EMPTY_FILE(2066, "请上传兑换码文件"),
    TIP_REDEEM_CODE_FILE_IS_EMPTY(2067, "兑换码文件数据为空"),
    TIP_REDEEM_CODE_ACTIVITY_ID_IS_EMPTY(2068, "未选择兑换码活动"),
    TIP_REDEEM_CODE_INPUT_EXCEL(2069, "请上传Excel文件"),
    TIP_REDEEM_CODE_ACTIVITY_IS_EMPTY(2079, "未选择兑换码活动"),
    TIP_REDEEM_CODE_WELCOME_MSG_IS_EMPTY(2080, "活动欢迎语，三个内容框必须有一个有内容"),
    TIP_REDEEM_CODE_FILE_DATA_IS_EMPTY(2081, "文件中数据为空"),
    TIP_REDEEM_CODE_ACTIVITY_LIMIT_ADD_USER(2082, "活动已开启参与限制，同一客户不能多次领取"),

    //员工模块错误码
    TIP_PARAM_MISSING(3001, "请填写完整的员工信息"),
    TIP_ERROR_UPLOAD_HEAD_IMG(3002, "上传头像失败"),
    TIP_USER_NOT_ACTIVE(3003, "员工不存在或者未激活"),
    TIP_HANDOVER_USER_ERROR(3004, "原跟进成员不存在或者错误"),
    TIP_TAKEOVER_USER_ERROR(3005, "接替成员不存在或者未激活"),
    TIP_CORP_NO_CUSTOMER_ASSIGNABLE(3006, "该企业不存在可分配的员工"),
    TIP_USER_NO_CUSTOMER_ASSIGNABLE(3007, "该成员不存在可分配的客户"),
    TIP_BIND_USER_MAPPING_ERROR(3008, "所在企业未绑定三方员工映射关系"),

    //客户中心返回码
    TIP_EXTENDS_CUSTOMER_FALSE(4000, "分配失败，请联系客服或技术人员"),
    TIP_SELECT_ALLOCATE_CUSTOMER(4001, "获取待分配列表失败，请联系客服或技术人员"),
    TIP_CUSTOMER_NOT_EXIST(4002, "客户不存在"),
    TIP_GROUP_TAG_CATEGORY_SAME_NAME(4003, "客户群标签组“%s”已存在"),
    TIP_GROUP_TAG_CATEGORY_OVER_MAX_SIZE(4004, "客户群标签组最多可设置3000个标签组"),
    TIP_GROUP_TAG_EXIST(4005, "存在标签重名！"),
    TIP_EXTEND_PROP_NAME_EXISTED(4006, "字段名称不能重复，请重新设置"),
    TIP_UPDATE_EXTEND_PROP_FAIL(4007, "更新客户自定义属性失败"),
    TIP_MULTIPLE_OPTION_MISSING(4008, "缺少多选选项值"),
    TIP_PROP_TYPE_CANNOT_CHANGE(4009, "标签类型无法编辑"),
    TIP_IS_SYS_PROP_NAME(4010, "该字段是系统默认字段名称,请重新设置"),
    TIP_CUSTOMER_CANNOT_BE_ASSIGNED(4011, "指定的客户不可被分配"),
    TIP_GROUP_CANNOT_BE_ASSIGNED(4012, "指定的群聊不可被分配"),
    TIP_CAN_TRANSFER_SELF_CUSTOMER(4013, "原跟进人与接手人一样，不可继承"),
    TIP_CANNOT_FIND_TRANSFER_RECORD(4014, "找不到该分配记录详情"),
    TIP_AUTO_TAG_KEYWORD_NUM_LIMIT(4015, "最多可设置10个关键词"),
    TIP_AUTO_TAG_KEYWORD_REPEATED(4016, "同一匹配方式下关键词不能重复"),
    TIP_AUTO_TAG_TAG_NUM_LIMIT(4017, "最多可设置10个标签"),
    TIP_AUTO_TAG_GROUP_SCENE_NUM_LIMIT(4018, "“最多可设置10个场景"),
    TIP_AUTO_TAG_SCENE_NOT_NULL(4019, "“请设置场景"),
    TIP_AUTO_TAG_SCENE_TAG_NOT_NULL(4020, "“请设置场景标签"),
    TIP_AUTO_TAG_SCENE_GROUP_NOT_NULL(4021, "“请设置场景群"),
    TIP_AUTO_TAG_SCENE_GROUP_NUM_LIMIT(4022, "最多可设置10个群聊"),
    TIP_AUTO_TAG_SCENE_TAG_NUM_LIMIT(4023, "最多可设置10个标签"),
    TIP_AUTO_TAG_MATCH_KEYWORD_NOT_BOTH_NULL(4024, "模糊匹配和精确匹配类型的关键词不能同时为空"),
    TIP_DELETE_TAG_NOT_PERMISSIONS(4025, "无权限操作标签，若标签不是在本系统创建，请前往企微后台操作"),

    //三方错误码
    TIP_MISSING_LOGIN_INFO(7000, "所在企业未开通「壹鸽快递工单助手」，请联系管理员"),
    TIP_UN_USE_AI_SYSTEM(7001, "您没有访问权限，请联系管理员"),
    TIP_UN_BIND_NETWORK(7002, "所在企业未开通「壹鸽快递工单助手」，请联系管理员"),


    //应用中心
    TIP_APP_INSTALLED(8000, "应用已安装"),
    TIP_NETWORK_ID_ERROR(8001, "未在工单系统找到与{0}匹配的企业，请填写有效网点ID"),
    TIP_ORDER_USER_ID_BIND_ERROR(8002, "账号已被绑定"),
    TIP_NETWORK_ID_BIND_ERROR(8003, "该企业未绑定网点ID"),
    TIP_YIGE_APP_CONFIG_ERROR(8004, "该企业未绑定网点"),
    TIP_YIGE_CHAT_BIND_ERROR(8005, "该群已绑定其它客户"),
    TIP_YIGE_CHAT_NOT_BIND_ERROR(8006, "该群未绑定客户"),
    TIP_YIGE_USER_NOT_BIND_ERROR(8007, "该员工未绑定客户"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String tipMsg;

    ResultTip(Integer code, String tipMsg) {
        this.code = code;
        this.tipMsg = tipMsg;
    }

    public static ResultTip getTip(Integer code) {
        if (ObjectUtils.isEmpty(code)) {
            return TIP_GENERAL_ERROR;
        }
        ResultTip[] values = ResultTip.values();

        for (ResultTip resultTip : values) {
            if (resultTip.getCode().equals(code)) {
                return resultTip;
            }
        }
        return TIP_GENERAL_ERROR;
    }
}
