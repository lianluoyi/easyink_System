package com.easywecom.wecom.domain.resp;

import lombok.Data;

/**
 * 类名: 生成小程序urllink响应参数
 *
 * @author : silver_chariot
 * @date : 2022/7/20 14:21
 **/
@Data
public class GenerateUrlLinkResp extends WechatOpenBaseResp {

    /**
     * 小程序 URL Link
     */
    private String url_link;
}