package com.easyink.wecom.domain.vo;

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

    @ApiModelProperty(value = "二维码状态:未扫码(QRCODE_SCAN_NEVER),二维码过期(QRCODE_EXPIRE),扫码成功(QRCODE_SCAN_SUCC),登录失败(QRCODE_LOGIN_FAIL),企业不匹配(QRCODE_LOGIN_FAIL_CORP_MISMATCH),需要短信验证（NEED_MOBILE_CONFIRM）")
    private String status;

    @ApiModelProperty(value = "短信验证需要的tlKey,只有需要短信验证的时候返回,调用短信验证接口的时候需要再携带返回")
    private String tlKey;

    @ApiModelProperty(value = "短信验证的手机号")
    private String tel;

    public WeCheckQrcodeVO(String status) {
        this.status = status;
    }

}
