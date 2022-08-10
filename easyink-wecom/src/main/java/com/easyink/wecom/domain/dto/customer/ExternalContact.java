package com.easyink.wecom.domain.dto.customer;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 类名: 企微API外部联系人实体
 *
 * @author : silver_chariot
 * @date : 2021/11/1 10:57
 */
@Data
public class ExternalContact {
    /**
     * 外部联系人userId
     */
    private String externalUserid;
    /**
     * 外部联系人名称
     */
    private String name;
    /**
     * 外部联系人职位
     */
    private String position = StringUtils.EMPTY;

    /**
     * 外部联系人头像
     */
    private String avatar;
    /**
     * 外部联系人所在企业简称
     */
    private String corpName = StringUtils.EMPTY;
    ;
    /**
     * 外部联系人所在企业全称
     */
    private String corpFullName = StringUtils.EMPTY;
    ;
    /**
     * 外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
     */
    private Integer type;
    /**
     * 外部联系人性别 0-未知 1-男性 2-女性
     */
    private Integer gender;
    /**
     * 外部联系人在微信开放平台的唯一身份标识（微信unionid），通过此字段企业可将外部联系人与公众号/小程序用户关联起来。
     */
    private String unionid = StringUtils.EMPTY;


}
