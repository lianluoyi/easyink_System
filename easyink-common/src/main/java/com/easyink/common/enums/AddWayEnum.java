package com.easyink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名: 客户来源枚举
 *
 * @author : silver_chariot
 * @date : 2021/12/3 10:55
 */
@AllArgsConstructor
@Getter
public enum AddWayEnum {

    UN_KNOWN(0, "未知来源"),
    SCAN(1, "扫描二维码"),
    MOBILE(2, "搜索手机号"),
    CARD(3, "名片分享"),
    MOBILE_CONTACT(5, "手机通讯录"),
    WE_CHAT_CONTACT(6, "微信联系人"),
    WECHAT(6, "微信联系人"),
    THIRD(8, "安装第三方应用时自动添加的客服人员"),
    EMAIL(9, "搜索邮箱"),
    VIDEO_OFFICIAL(10, "视频号主页添加"),
    CUSTOMER_ACQUISITION(16, "通过获客链接添加"),
    ALL_ADD_WAY(17, "所有来源"),
    INNER_USER_SHARE(201, "内部成员共享"),
    TRANSFER(202, "管理员/负责人分配");
    /**
     * 状态码
     */
    public final Integer code;
    /**
     * 含义
     */
    private final String desc;
}
