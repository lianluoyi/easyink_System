package com.easyink.common.constant;

public class GroupConstants {
    /**
     * 群组状态
     * 0 - 正常;1 - 跟进人离职;2 - 离职继承中;3 - 离职继承完成;
     */
    public static final Integer NARMAL = 0;
    public static final Integer OWNER_LEAVE = 1;
    public static final Integer OWNER_LEAVE_EXTEND = 2;
    public static final Integer OWNER_LEAVE_EXTEND_SUCCESS = 3;


    /**
     * 1-代表群聊成员是员工    2-代码群聊成员是客户
     */
    public static final Integer ID_TYPE_USER = 1;
    public static final Integer ID_TYPE_CUSTOMER = 2;

    public static final Integer MAX_ALLOCATE_GROUP = 300;

    public static final String UNKNOW_GROUP_NAME = "未知群聊";


    private GroupConstants() {
    }
}
