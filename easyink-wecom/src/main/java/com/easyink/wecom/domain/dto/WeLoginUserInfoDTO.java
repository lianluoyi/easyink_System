package com.easyink.wecom.domain.dto;

import lombok.Data;

/**
 * 类名: WeLoginUserInfoDTO
 *
 * @author: 1*+
 * @date: 2021-09-10 15:24
 */
@Data
public class WeLoginUserInfoDTO extends WeResultDTO {

    /**
     * 登录用户的类型：1.创建者 2.内部系统管理员 3.外部系统管理员 4.分级管理员 5.成员
     */
    private Integer usertype;
    /**
     * 登录用户的信息
     */
    private UserInfo user_info;
    /**
     * 授权方企业信息
     */
    private CorpInfo corp_info;

    @Data
    public class UserInfo {
        /**
         * 登录用户的userid，登录用户在通讯录中时返回
         */
        private String userid;
        /**
         * 登录用户的名字，登录用户在通讯录中时返回，此字段从2019年12月30日起，对新创建服务商不再返回，2020年6月30日起，对所有历史服务商不再返回真实name，使用userid代替name返回，第三方页面需要通过通讯录展示组件来展示名字
         */
        private String name;
        /**
         * 登录用户的头像，登录用户在通讯录中时返回
         */
        private String avatar;
    }

    @Data
    public class CorpInfo {
        /**
         * 授权方企业id
         */
        private String corpid;
    }
}
