package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 检测二维码返回
 *
 * @author: 1*+
 * @date: 2021-08-25$ 17:45$
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("检测二维码返回实体")
public class WeCheckQrcodeVO {

    @ApiModelProperty(value = "二维码状态:未扫码(QRCODE_SCAN_NEVER),二维码过期(QRCODE_EXPIRE),扫码成功(QRCODE_SCAN_SUCC),登录失败(QRCODE_LOGIN_FAIL),企业不匹配(QRCODE_LOGIN_FAIL_CORP_MISMATCH)")
    private String status;


}
