package com.easywecom.common.enums;

/**
 * @author 佚名
 * @ClassName StaffActivateEnum
 * @date 2021/8/10 16:34
 * @Version 1.0
 */
public enum StaffActivateEnum {
    /**
     * 已激活
     */
    ACTIVE(1, "已激活"),
    /**
     * 已禁止
     */
    FORBIDDEN(2, "已禁止"),
    /**
     * 未激活
     */
    NOT_ACTIVE(4, "未激活"),
    /**
     * 离职
     */
    RETIRE(5, "离职"),
    /**
     * 已删除
     */
    DELETE(6, "已删除"),
    ;
    private final Integer code;
    private final String info;

    StaffActivateEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
