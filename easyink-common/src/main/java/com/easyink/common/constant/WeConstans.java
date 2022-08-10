package com.easyink.common.constant;


import lombok.Getter;

/**
 * @author admin
 * @description: 企业微信相关常量
 * @create: 2020-08-26 17:01
 **/
public class WeConstans {
    public static final String SLASH = "/";
    public static final String VERTICAL_BAR = "|";
    public static final String SUFFIX = "等";
    /**
     * 侧边栏顺序
     * 0 海报、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 、5图文链接、6小程序
     */
    public static final String SIDE_SEQ = "0,5,2,3,6";
    //MAP初始化大小
    public static final int INIT_MAP_CAPACITY = 16;
    //消息最大附件数量
    public static final int MAX_ATTACHMENT_NUM = 9;
    // 群欢迎语最大附件数量
    public static final int GROUP_MAX_ATTACHMENT_NUM = 1;
    /**
     * 表示成员的入群方式
     * 0 - 由成员邀请入群（包括直接邀请入群和通过邀请链接入群）
     * 3 - 通过扫描群二维码入群
     */
    public static final String CODE_JOIN_SCENE = "3";

    /**
     * 异步任务完成通知
     * sync_user:增量更新成员
     * replace_user:全量覆盖成员
     * invite_user:=邀请成员关注
     * replace_party:全量覆盖部门
     */
    public static final String SYNC_USER = "sync_user";
    public static final String REPLACE_USER = "replace_user";
    public static final String INVITE_USER = "invite_user";
    public static final String REPLACE_PARTY = "replace_party";


    /**
     * 查询对应Serverurl和Prefix
     */
    public static final String WECOM_SERVER_URL = "weComServerUrl";
    public static final String WECOM_PREFIX = "weComePrefix";
    public static final String ORDER_ACCESS_ID = "orderAccessId";
    public static final String ORDER_ACCESS_KEY = "orderAccessKey";
    public static final String ORDER_SERVER_URL = "orderServerUrl";


    /**
     * 索引对应数据
     */
    public static final String FROM = "from";
    public static final String FROMM_INFO = "fromInfo";
    public static final String TO_LIST = "tolist";
    public static final String ROOMID = "roomid";
    public static final String ROOM_INFO = "roomInfo";
    public static final String TO_LIST_INFO = "toListInfo";
    public static final String MSG_TIME = "msgtime";
    public static final String MSG_TYPE = "msgtype";
    public static final String STATUS = "status";
    public static final String CONTENT = "content";
    public static final String PATTERN_WORDS = "pattern_words";
    public static final String MSG_ID = "msgid";
    public static final String PRE_MSG_ID = "revoke.pre_msgid";
    public static final String SEQ = "seq";
    public static final String ACTION = "action";
    public static final String PROPERTIES = "properties";
    public static final String TEXT = "text";

    /**
     * 侧边栏判断是否全选：0 全选 1 非全选
     */
    public static final String CHECK_ALL = "0";
    public static final String NO_CHECK_ALL = "1";

    /**
     * 微信授权token
     */
    public static final String WX_AUTH_ACCESS_TOKEN = "wx_auth_access_token";
    public static final String WX_AUTH_REFRESH_ACCESS_TOKEN = "wx_auth_refresh_access_token";

    /**
     * 微信通用token
     */
    public static final String WX_ACCESS_TOKEN = "wx_access_token";

    /**
     * 企业微信相关token
     */
    public static final String WE_COMMON_ACCESS_TOKEN = "we_common_access_token";


    /**
     * 自建应用token
     */
    public static final String WE_THIRD_APP_TOKEN = "we_third_app_token";


    /**
     * 获取外部联系人相关 token
     */
    public static final String WE_CONTACT_ACCESS_TOKEN = "we_contact_access_token";
    /**
     * 获取外部联系人相关 token
     */
    public static final String WE_CUSTOM_ACCESS_TOKEN = "we_custom_access_token";

    /**
     * 供应商相关token
     */
    public static final String WE_PROVIDER_ACCESS_TOKEN = "we_provider_access_token";

    /**
     * 会话存档相关token
     */
    public static final String WE_CHAT_ACCESS_TOKEN = "we_chat_access_token";

    /**
     * 应用相关token
     */
    public static final String WE_AGENT_ACCESS_TOKEN = "we_agent_access_token";

    /**
     * 三方应用相关token
     */
    public static final String WE_SUITE_ACCESS_TOKEN = "we_suite_access_token:";

    /**
     * 三方应用授权企业相关token
     */
    public static final String WE_AUTH_CORP_ACCESS_TOKEN = "we_auth_corp_access_token:";


    public static final String WE_EMPLE_CODE_KEY = "we_emple_code_key";

    public static final String WE_ACTUAL_GROUP_CODE_KEY = "we_actual_group_code_key";
    /**
     * 企业配置缓存
     */
    public static final String WE_CORP_ACCOUNT = "CurrentWeCorpAccount-new2";

    /**
     * 服务商ticket缓存
     */
    public static final String WE_SUITE_TICKET = "we_suite_ticket:";


    /**
     * 企业微信接口返回成功code
     */
    public static final Integer WE_SUCCESS_CODE = 0;


    /**
     * 企业微信端根部门id
     */
    public static final Long WE_ROOT_DEPARMENT_ID = 1L;


    /**
     * 企业微信通讯录用户启用
     */
    public static final Integer WE_USER_START = 1;


    /**
     * 企业微信通讯录用户停用
     */
    public static final Integer WE_USER_STOP = 0;


    /**
     * 同步功能提示语
     */
    public static final String SYNCH_TIP = "后台开始同步数据，请稍后关注进度";


    /**
     * 离职未分配
     */
    public static final Integer LEAVE_NO_ALLOCATE_STATE = 0;


    /**
     * 离职已分配分配
     */
    public static final Integer LEAVE_ALLOCATE_STATE = 1;


    /**
     * 已激活
     */
    public static final Integer WE_USER_IS_ACTIVATE = 1;


    /**
     * 已禁用
     */
    public static final Integer WE_USER_IS_FORBIDDEN = 2;


    /**
     * 已离职
     */
    public static final Integer WE_USER_IS_LEAVE = 6;


    /**
     * 未激活
     */
    public static final Integer WE_USER_IS_NO_ACTIVATE = 4;


    /**
     * 企业微信素材目录根id
     */
    public static final Long WE_ROOT_CATEGORY_ID = 0L;

    /**
     * 实际群活码正在使用中
     */
    public static final Integer WE_GROUP_CODE_ENABLE = 0;

    /**
     * 群活码已禁用/达到扫码次数上限
     */
    public static final Integer WE_GROUP_CODE_DISABLE = 1;


    /**
     * 单人活码
     */
    public static final Integer SINGLE_EMPLE_CODE_TYPE = 1;


    /**
     * 多人活码
     */
    public static final Integer MANY_EMPLE_CODE_TYPE = 2;


    /**
     * 批量单人活码
     */
    public static final Integer BATCH_SINGLE_EMPLE_CODE_TYPE = 3;


    /**
     * 在小程序中联系场景
     */
    public static final Integer SMALL_ROUTINE_EMPLE_CODE_SCENE = 1;


    /**
     * 通过二维码联系场景
     */
    public static final Integer QR_CODE_EMPLE_CODE_SCENE = 2;

    /**
     * 客户添加时无需经过确认自动成为好友,是
     */
    public static final Boolean IS_JOIN_CONFIR_MFRIENDS = true;

    /**
     * 客户添加时无需经过确认自动成为好友,否
     */
    public static final Boolean NOT_IS_JOIN_CONFIR_MFRIENDS = false;

    /**
     * 批量生成的单人码 活动场景
     */
    public static final String ONE_PERSON_CODE_GENERATED_BATCH = "批量生成的单人码";

    /**
     * 微信接口相应端错误字段
     */
    public static final String WE_ERROR_FIELD = "errcode";


    /**
     * 递归
     */
    public static final Integer YES_IS_RECURSION = 0;


    /**
     * 获取所有子部门数据
     */
    public static final Integer DEPARTMENT_SUB_WEUSER = 1;


    /**
     * 获取当前部门
     */
    public static final Integer DEPARTMENT_CURRENT_WEUSER = 0;


    /**
     * 通讯录用户激活
     */
    public static final Integer YES_IS_ACTIVATE = 1;


    /**
     * 通讯录用户未激活
     */
    public static final Integer NO_IS_ACTIVATE = 2;


    /**
     * 不存在外部联系人的关系
     */
    public static final Integer NOT_EXIST_CONTACT = 84061;

    public static final String COMMA = ",";

    public static final String USER_ID = "userid";

    public static final String CURSOR = "cursor";

    public static final String CORPID = "CORP_ID";

    public static final String PAGE_ID = "page_id";

    public static final String PAGE_SIZE = "page_size";

    /**
     * 获取客户详情并同步客户数据
     */
    public static final String SYNCH_WE_CUSTOMER_ADD = "0";

    public static final String SYNCH_WE_CUSTOMER_UPDATE = "1";
    /**
     * 业务id类型1:组织机构id,2:成员id
     */
    public static final Integer USE_SCOP_BUSINESSID_TYPE_USER = 2;
    public static final Integer USE_SCOP_BUSINESSID_TYPE_ORG = 1;
    public static final Integer USE_SCOP_BUSINESSID_TYPE_ALL = 3;

    /**
     * 客户流失通知开关 0:关闭 1:开启
     */
    public static final String DEL_FOLLOW_USER_SWITCH_CLOSE = "0";
    public static final String DEL_FOLLOW_USER_SWITCH_OPEN = "1";

    public static final String CONTACT_SEQ_KEY = "seq";

    public static String getContactSeqKey(String corpId) {
        return CONTACT_SEQ_KEY + ":" + corpId;
    }

    /**
     * id类型 0:成员 1:客户,2:机器
     */
    public static final Integer ID_TYPE_USER = 0;
    public static final Integer ID_TYPE_EX = 1;
    public static final Integer ID_TYPE_MACHINE = 2;

    /**
     * 一次拉取的消息条数，最大值1000条，超过1000条会返回错误
     */
    public static final long LIMIT = 1_000L;

    /**
     * 敏感词过滤查询用户分片
     */
    public static final Integer SENSITIVE_USER_PIECE = 50;

    /**
     * 任务裂变用户活码state前缀
     */
    public static final String FISSION_PREFIX = "fis-";

    public static final String APP_TICKET_KEY = "ticket:AppGet";
    public static final String AGENT_TICKET_KEY = "ticket:AgentGet";


    //性别，1表示男性，2表示女性
    //表示所在部门是否为上级，0-否，1-是，顺序与Department字段的部门逐一对应
    //激活状态：1=已激活 2=已禁用 4=未激活 已激活代表已激活企业微信或已关注微工作台（原企业号） 5=成员退出
    public enum corpUserEnum {

        USER_SEX_TYPE_MAN(1, "男性"),
        USER_SEX_TYPE_WEMAN(2, "女性"),

        IS_DEPARTMENT_SUPERIOR_YES(1, "是"),
        IS_DEPARTMENT_SUPERIOR_NO(0, "否"),

        ACTIVE_STATE_ONE(1, "已激活"),
        ACTIVE_STATE_TWO(2, "已禁用"),
        ACTIVE_STATE_FOUR(4, "未激活"),
        ACTIVE_STATE_FIVE(5, "成员退出");

        private Integer key;
        private String value;

        /**
         * 构造方法
         *
         * @param key
         * @param value
         */
        corpUserEnum(int key, String value) {
            this.setKey(key);
            this.setValue(value);
        }

        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    @Getter
    public enum sendMessageStatusEnum {
        NOT_SEND("0", "未发送"),
        SEND("1", "已发送"),
        NOT_FRIEND_SEND("2", "因客户不是好友导致发送失败"),
        RECEIVE_OTHER_MESSAGE("3", "-因客户已经收到其他群发消息导致发送失败"),
        ;

        private String status;
        private String desc;

        /**
         * 构造方法
         *
         * @param status
         * @param desc
         */
        sendMessageStatusEnum(String status, String desc) {
            this.status = status;
            this.desc = desc;
        }
    }


    public static final String WECOM_SENSITIVE_HIT_INDEX = "sensitive";

    /**
     * 获取敏感词索引(字母小写化)
     */
    public static String getWecomSensitiveHitIndex(String corpId) {
        return (WECOM_SENSITIVE_HIT_INDEX + "-" + corpId).toLowerCase();
    }

    public static final String WECOM_CHAT_DATA_INDEX = "chatdata";

    /**
     * 获取会话存档索引(字母小写化)
     */
    public static String getChatDataIndex(String corpId) {
        return (WECOM_CHAT_DATA_INDEX + "-" + corpId).toLowerCase();
    }

    /**
     * 开启会话存档成员列表
     **/
    public static final String WE_MSG_AUDIT_KEY = "wecom_msg_audit:user:ids";


    /**
     * 第三方应用ID，参数标实
     */
    public static final String THIRD_APP_PARAM_TIP = "agentId";

    public static final String WECUSTOMERS_KEY = "weCustomer:";

    /**
     * 群发数据常量
     * 发给客户
     */
    public static final String SEND_MESSAGE_CUSTOMER = "0";

    /**
     * 发给客户群
     */
    public static final String SEND_MESSAGE_GROUP = "1";

    /**
     * 定时发送
     */
    public static final String SEND_MESSAGE_JOB = "2";

    /**
     * 消息范围 0 全部客户
     */
    public static final String SEND_MESSAGE_CUSTOMER_ALL = "0";

    /**
     * 查询全部的客户列表
     */
    public static final String QUERY_ALL = "all";

    /**
     * 消息范围 1 指定客户
     */
    public static final String SEND_MESSAGE_CUSTOMER_PART = "1";

    /**
     * 是否删除：0 未删除 、1 已删除
     */
    public static final Integer WE_CUSTOMER_MSG_RESULT_NO_DEFALE = 0;
    public static final Integer WE_CUSTOMER_MSG_RESULT_DEFALE = 1;

    /**
     * 是否开启会话存档 0：关闭 1：开启
     */
    public static final Integer OPEN_CHAT = 1;
    public static final Integer CLOSE_CHAT = 0;

    /**
     * 客户员工关系表：
     * createTime创建事假，
     * total总数
     */

    /**
     * 判断是否需要获取群成员名字
     */
    public static final Integer NEED_NAME = 1;

    /**
     * 判断是否是群聊
     */
    public static final Integer IS_ROOM = 1;

    /**
     * 员工触发敏感词 消息通知审计人 1-开启通知
     */
    public static final Integer SENSITIVE_NOTICE = 1;
    /**
     *
     */
    public static final String EXTENDS_OTHER_ENVIRONMENT = "已在客户端或第三方分配完成";

    /**
     * 保存员工活码key
     *
     * @param corpId   企业Id
     * @param configId 二维码configId
     * @return we_emple_code_key:{corpId}:{configId}
     */
    public static String getWeEmployCodeKey(String corpId, String configId) {
        return WE_EMPLE_CODE_KEY + ":" + corpId + ":" + configId;
    }

    /**
     * 保存实际群码key
     *
     * @param actualId 实际群码id
     * @return we_actual_group_code_key:{actualId}
     */
    public static String getWeActualGroupCodeKey(String actualId) {
        return WE_ACTUAL_GROUP_CODE_KEY + ":" + actualId;
    }

    /**
     * 话术库根节点名称(默认:全部)
     */
    public static final String DEFAULT_WE_WORDS_CATEGORY_ROOT_NAME = "全部";

    /**
     * 素材配置定时清理天数(默认7)
     */
    public static final Integer DEFAULT_WE_MATERIAL_DEL_DAYS = 7;

    /**
     * 素材配置(默认不删除)
     */
    public static final Boolean DEFAULT_WE_MATERIAL_NOT_DEL = false;

    /**
     * 素材发布状态(默认发布到侧边栏)
     */
    public static final Boolean DEFAULT_WE_MATERIAL_USING = true;

    /**
     * 素材发布状态(不发布)
     */
    public static final Boolean WE_MATERIAL_NOT_USING = false;

    /**
     * 素材过期时间(默认不过期)
     */
    public static final String DEFAULT_MATERIAL_NOT_EXPIRE = "2099-01-01 00:00:00";
    /**
     * 群发立即发送
     */
    public static final int NORMAL_TASK = 0;
    /**
     * 群发定时任务
     */
    public static final int TIME_TASK = 1;
    /**
     * 群发未发送
     */
    public static final String NOT_SEND = "0";
    /**
     * 群发已发送
     */
    public static final String SEND = "1";

    /**
     * 话术库上级文件夹ID(默认：0)
     */
    public static final Long DEFAULT_WE_WORDS_CATEGORY_PARENT_ID = 0L;

    /**
     * 话术库最高排序(默认：0)
     */
    public static final Integer DEFAULT_WE_WORDS_CATEGORY_HIGHEST_SORT = 0;
    /**
     * 素材未过期
     */
    public static final Boolean MATERIAL_UN_EXPIRE = false;

    /**
     * 素材类型-发布
     */
    public static final Boolean WE_CATEGORY_USING = true;

    /**
     * 默认值-空字符串
     */
    public static final String DEFAULT_EMPTY_STRING = "";
    /**
     * 根部门
     */
    public static final String ROOT_DEPARTMENT = "1";


    /**
     * 默认企微发视频大小 10M
     */
    public static final Long DEFAULT_MAX_VIDEO_SIZE = 10 * 1024 * 1024L;
    /**
     * 默认企微发图片大小 10M
     */
    public static final Long DEFAULT_MAX_IMAGE_SIZE = DEFAULT_MAX_VIDEO_SIZE;
    /**
     * 默认企微发文件大小 20M
     */
    public static final Long DEFAULT_MAX_FILE_SIZE = 2 * 10 * 1024 * 1024L;
    /**
     * 朋友圈默认图片宽度
     */
    public static final int DEFAULT_MAX_IMAGE_WIDTH = 1440;
    /**
     * 朋友圈默认图片高度
     */
    public static final int DEFAULT_MAX_IMAGE_HEIGHT = 1080;

    /**
     * 客户昵称替换
     */
    public static final String CUSTOMER_NICKNAME = "#客户昵称#";

    /**
     * 员工姓名替换
     */
    public static final String EMPLOYEE_NAME = "#员工姓名#";

    /**
     * 群欢迎语客户昵称占位符
     */
    public static final String GROUP_CUSTOMER_NICKNAME = "%NICKNAME%";

    /**
     * 点击查看视频
     */
    public static final String CLICK_SEE_VIDEO = "点击查看视频";

    /**
     * 默认视频封面
     */
    public static final String DEFAULT_VIDEO_COVER_URL = "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/11/08/%E8%A7%86%E9%A2%91.png";

    /**
     * 默认群活码的mediaType=-1
     */
    public static final Integer DEFAULT_GROUP_CODE_MEDIA_TYPE = -1;
    /**
     * 第一个分页
     */
    public static final Integer FIRST_PAGE = 1;

    /**
     * 话术详情附件文本类型
     */
    public static final Integer WE_WORDS_DETAIL_MEDIATYPE_TEXT = 4;


    /**
     * 客户群标签最大创建数量(默认值3000)
     */
    public static final Integer DEFAULT_WE_GROUP_TAG_CATEGORY_SIZE = 3000;

    /**
     * 成员类型:1 企业成员
     */
    public static final Integer WE_GROUP_MEMBER_TYPE_STAFF = 1;

    /**
     * 成员类型:2 外部联系人
     */
    public static final Integer WE_GROUP_MEMBER_TYPE_CUSTOMER = 2;

    /**
     * 发送信息模板
     */
    public static final String CUSTOMER_GROUP_MESSAGE_INFO = "【任务提醒】\n" +
            "\n" +
            "任务类型：群发任务\n" +
            "\n" +
            "发送对象：【替换内容】\n" +
            "\n" +
            "请及时前往【客户群】中确认发送";
    public static final String CUSTOMER_MESSAGE_INFO = "【任务提醒】\n" +
            "\n" +
            "任务类型：群发任务\n" +
            "\n" +
            "发送对象：【替换内容】\n" +
            "\n" +
            "请及时前往【客户联系】中确认发送";
    public static final String REPLACE_MSG = "【替换内容】";
    /**
     * mediaId 缓存key
     */
    public static final String MEDIA_KEY = "media_url:";
    /**
     * 朋友圈mediaId 缓存key
     */
    public static final String MOMENT_ATTACHMENT_MEDIA_KEY = "moment_attachment_media_url:";


    public static final String REVOKE_KEY = "revoke_msgid:";

    /**
     * 老客进群，发送失败过滤条件 （isSend=2）
     */
    public static final Integer SEND_CORP_MESSAGE_ISSEND_FAIL = 2;

    /**
     * 默认运营中心SOP的话术groupId为-1
     */
    public static final Long DEFAULT_SOP_WORDS_DETAIL_GROUP_ID = -1L;

    /**
     * 运营中心SOP 默认开始时间
     */
    public static final String DEFAULT_SOP_START_TIME = "1970-01-01 00:00:00";

    /**
     * 运营中心SOP 默认结束时间
     */
    public static final String DEFAULT_SOP_END_TIME = "2099-01-01 00:00:00";

    /**
     * SOP 详情 对应字段is_finish , 是否已执行 0：未执行，1：已执行
     */
    public static final Integer UN_EXECUTE = 0;
    public static final Integer EXECUTE = 1;

    /**
     * SOP任务详情的type的过滤值定义
     */
    public static final String CUSTOMER_SOP_TYPE_VAL = "2,3,4";
    public static final String GROUP_SOP_TYPE_VAL = "0,1";
    public static final String GROUP_CALENDAR_TYPE_VAL = "5";

    /**
     * 运营中心 新增SOP 使用员工/部门 对应字段type 1:使用部门,0:使用员工
     */
    public static final Integer SOP_USE_DEPARTMENT = 1;
    public static final Integer SOP_USE_EMPLOYEE = 2;

    /**
     * 默认alterData1值
     */
    public static final Integer DEFAULT_SOP_ALTER_DATA1 = 0;
    /**
     * 接替中的备注提示
     */
    public static final String DEFAULT_TRANSFER_NOTICE = "已发起接替请求，24小时后自动接替";
    /**
     * 待开发应用订阅锁key
     */
    public static final String SUBSCRIBE_KEY = "subscribe:";
    /**
     * 待开发应用订阅锁时长
     */
    public static final Long SUBSCRIBE_EXPIRE_TIME = 60L;

    /**
     * 个人朋友圈消息模板
     */
    public static final String PERSONAL_MOMENT_MSG = "【朋友圈】\n" +
            "\n" +
            "提醒内容：您有一条发送朋友圈的任务\n" +
            "\n" +
            "创建时间：{0}\n" +
            "\n" +
            "{1}";

    public static final String ENTERPRISE_MOMENT_USER_MSG = "【朋友圈】\n" +
            "\n" +
            "提醒内容：您有一条发送朋友圈的任务\n" +
            "\n" +
            "创建时间：{0}\n" +
            "\n" +
            "请从企业微信APP前往【客户朋友圈】确认发送，记得及时完成喔！";

    public static final String PERSONAL_MOMENT_USER_MSG = "【朋友圈】\n" +
            "\n" +
            "提醒内容：您有一条发送朋友圈的任务\n" +
            "\n" +
            "创建时间：{0}\n" +
            "\n" +
            "{1}";
    public static final String  MOMENT_NO_CUSTOMER ="该员工没有需触达客户";
    public static final String VIDEO_2_LINK_DEFAULT_URL = "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/01/19/defauluVedio.jpg";

    /**
     * 自动标签关键词数量限制
     */
    public static final int AUTO_TAG_KEYWORD_NUM_LIMIT = 10;

    /**
     * 自动标签标签数量限制
     */
    public static final int AUTO_TAG_TAG_NUM_LIMIT = 10;

    /**
     * 自动标签群场景数量限制
     */
    public static final int AUTO_TAG_GROUP_SCENE_NUM_LIMIT = 10;
    /**
     * 自动标签群场景群聊数量限制
     */
    public static final int AUTO_TAG_GROUP_SCENE_GROUP_NUM_LIMIT = 10;
    /**
     * 自动标签群场景标签数量限制
     */
    public static final int AUTO_TAG_GROUP_SCENE_TAG_NUM_LIMIT = 10;
    /**
     * 自动标签新客场景数量限制
     */
    public static final int AUTO_TAG_CUSTOMER_SCENE_NUM_LIMIT = 10;
    /**
     * 自动标签新客场景标签数量限制
     */
    public static final int AUTO_TAG_CUSTOMER_SCENE_TAG_NUM_LIMIT = 10;

    /**
     * 自动标签新建传入员工
     */
    public static final int AUTO_TAG_ADD_USER_TYPE = 2;
    /**
     * 自动标签传入部门
     */
    public static final int AUTO_TAG_ADD_DEPARTMENT_TYPE = 1;




}
