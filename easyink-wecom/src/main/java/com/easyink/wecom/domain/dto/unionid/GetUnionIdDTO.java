package com.easyink.wecom.domain.dto.unionid;

import lombok.Data;

/**
 * 类名: 根据external_user_id获取unionId的请求参数
 *
 * @author : silver_chariot
 * @date : 2023/1/5 9:26
 **/
@Data
public class GetUnionIdDTO {
    /**
     * 企微外部联系人userid
     */
    private String externalUserId;
    /**
     * 企业corpId
     */
    private String corpId;
    /**
     * 企业corpSecret
     */
    private String corpSecret;
}
