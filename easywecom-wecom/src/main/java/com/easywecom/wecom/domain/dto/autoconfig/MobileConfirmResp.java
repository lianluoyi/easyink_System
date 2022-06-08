package com.easywecom.wecom.domain.dto.autoconfig;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 类名: 登录后需要短信验证的响应
 * 响应示例：
 * {"redirect_url":"https://work.weixin.qq.com/wework_admin/login/choose_corp?tl_key=0fa11467f78fbc9c349c03170b65acba",
 * "tl_key":"0fa11467f78fbc9c349c03170b65acba","mobile":"180****9316","countingSeconds":60}
 *
 * @author : silver_chariot
 * @date : 2022/5/30 10:32
 */
@Data
public class MobileConfirmResp {
    /**
     * 需要重定向的url
     */
    @JSONField(name = "redirect_url")
    private String redirectUrl;
    /**
     * tlkey 后续验证需要带上这个参数
     */
    @JSONField(name = "tl_key")
    private String tlKey;
    /**
     * 需要验证的手机号
     */
    private String mobile;
    /**
     * 倒计时描述
     */
    private Integer countingSeconds;
}
