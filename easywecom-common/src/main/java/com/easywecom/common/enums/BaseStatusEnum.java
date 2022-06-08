package com.easywecom.common.enums;

/**
 * 基础的0，1状态枚举类
 *
 * @author 佚名
 * @ClassName BaseStatusEnum
 * @date 2021/8/10 16:51
 * @Version 1.0
 */
public enum BaseStatusEnum {
    /**
     * 开启
     */
    OPEN(1,"开启"),

    /**
     * 关闭
     */
    CLOSE(0,"关闭");
    private final Integer code;
    private final String info;

    BaseStatusEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }}
