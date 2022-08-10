package com.easywecom.wecom.domain.dto.autoconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 企业微信后台二维码Key获取
 *
 * @author: 1*+
 * @date: 2021-08-23 10:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeGetKeyResp {

    private String qrcode_key;
}
