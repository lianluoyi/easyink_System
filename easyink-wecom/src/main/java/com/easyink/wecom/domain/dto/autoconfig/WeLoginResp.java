package com.easyink.wecom.domain.dto.autoconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WeLoginResp
 *
 * @author: 1*+
 * @date: 2021-10-13 10:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeLoginResp {

    /**
     * 扫码登录用户的企业微信原生ID：168xxxxxxxx
     */
    private Long vid;
    /**
     * 企业微信原生ID：197xxxxxxx
     */
    private Long corpId;
    /**
     * 对应企业官方接口的企业ID:ww6xxxxxx
     */
    private String encodeCorpId;
    /**
     * 根部门名称
     */
    private String rootDepartName;
    /**
     * 企业简称
     */
    private String corpAlias;
    /**
     * 企业全称
     */
    private String corpFullName;
    /**
     * 企业Logo
     */
    private String logoPath;
    /**
     * 企业注册地址
     */
    private String corpAddress;
    /**
     * 企业手机号
     */
    private String mobile;
    /**
     * 登录人昵称
     */
    private String name;

}
