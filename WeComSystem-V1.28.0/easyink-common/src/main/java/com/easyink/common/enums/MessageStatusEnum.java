package com.easyink.common.enums;

import lombok.Getter;

/**
 * 类名： MessageStatusEnum
 *
 * @author 佚名
 * @date 2021/11/16 14:05
 */
@Getter
public enum MessageStatusEnum {
    /**
     * （员工未执行群发操作）
     */
    NOT_SEND("0", "未执行"),
    /**
     * （员工执行群发并成功送达客户）
     */
    SEND_SUCCEED("1", "发送成功"),
    /**
     * （员工执行群发，但客户已不是好友）
     */
    NOT_FRIEND("2", "已不是好友"),
    /**
     * （员工执行群发，但本次群发中其他员工对该客户执行了群发）
     */
    ALREADY_SEND("3", "其他员工已发送"),
    /**
     * （该客户的群发接收次数上限）
     */
    MAX_TIMES("4", "接收已达上限"),
    /**
     * （小程序不可发送）
     */
    MINI_PROGRAM_ERROR("5", "创建失败，小程序未关联企业或信息错误"),
    /**
     * （获取附件失败）
     */
    MEDIA_ID_ERROR("6", "创建失败，未获取到有效附件信息"),
    /**
     * （当员工对群群发时只选择了部分群，剩余群未发送）
     */
    NOT_SEND_GROUP("7", "未发送"),
    PARAM_ERROR("8", "创建失败，群发内容异常");


    private final String name;

    private final String type;

    MessageStatusEnum(String type, String name) {
        this.name = name;
        this.type = type;
    }
}
