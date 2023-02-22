package com.easyink.wecom.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 获取appid结果
 *
 * @author : silver_chariot
 * @date : 2022/7/25 17:54
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppIdVO {
    /**
     * appid
     */
    private String appId;

    /**
     * componentAppid (待开发需要返回)
     */
    private String componentAppId;

    /**
     * 企业id
     */
    private String corpId;
}
