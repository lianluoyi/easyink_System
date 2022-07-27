package com.easywecom.wecom.domain.resp;

import lombok.Data;

/**
 * 类名: 微信公众平台基础响应实体
 *
 * @author : silver_chariot
 * @date : 2022/7/20 14:22
 **/
@Data
public class WechatOpenBaseResp {

    /**
     * 错误码 （只有错误的时候返回) 默认为
     */
    private Integer errcode;
    /**
     * 错误消息 (只有错误的时候返回)
     */
    private String errmsg;


    /**
     * 是否返回错误信息
     *
     * @return true 是
     */
    public boolean isError() {
        return errcode != null || errmsg != null;
    }
}
