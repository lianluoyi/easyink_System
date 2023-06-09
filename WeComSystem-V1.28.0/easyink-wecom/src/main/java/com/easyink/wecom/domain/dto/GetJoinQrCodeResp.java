package com.easyink.wecom.domain.dto;

import lombok.Data;

/**
 * 类名: 获取加入企业二维码响应
 *
 * @author : silver_chariot
 * @date : 2021/11/3 10:08
 */
@Data
public class GetJoinQrCodeResp extends WeResultDTO {
    /**
     * 二维码链接，有效期7天
     */
    private String join_qrcode;
}
