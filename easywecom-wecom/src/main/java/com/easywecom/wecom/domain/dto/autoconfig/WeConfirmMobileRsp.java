package com.easywecom.wecom.domain.dto.autoconfig;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 类名: 短信验证响应体
 *
 * @author : silver_chariot
 * @date : 2022/6/6 16:10
 */
@Data
public class WeConfirmMobileRsp {
    /**
     * 跳转url
     */
    private String redirect_url;
    /**
     * tl key 用于验证接口
     */
    @JSONField(name = "tl_key")
    private String tlKey;
    /**
     * 需要验证的手机号
     */
    private String mobile;
    /**
     * 倒计时时间
     */
    private Integer countingSeconds;
}
