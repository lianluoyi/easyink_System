package com.easyink.wecom.factory.impl;

import com.easyink.common.redis.WechatOpenConfigRedisCache;
import com.easyink.wecom.domain.vo.WeOpenXmlMessageVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.factory.WeOpenCallBackEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 第三方平台验证票据
 * 在第三方平台创建审核通过后，微信服务器会向其 ”授权事件接收URL” 每隔 10 分钟以 POST 的方式推送 component_verify_ticket
 * https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/2.0/api/Before_Develop/component_verify_ticket.html
 *
 * @author wx
 * 2023/1/9 16:46
 **/
@Service("component_verify_ticket")
@RequiredArgsConstructor
@Slf4j
public class WeEvent3rdPlatformComponentVerifyTicketImpl implements WeOpenCallBackEventFactory {

    private final WechatOpenConfigRedisCache redisCache;

    @Override
    public void eventHandle(WeOpenXmlMessageVO message) {
        String appId = message.getAppId();
        String componentVerifyTicket = message.getComponentVerifyTicket();
        if (StringUtils.isBlank(componentVerifyTicket)) {
            log.info("[微信第三方平台-验证票据] 失败， componentVerifyTicket为空");
            return;
        }
        redisCache.setComponentVerifyTicket(appId, componentVerifyTicket);
        log.info("[微信第三方平台-验证票据] 成功, appid：{}, componentVerifyTicket:{}", appId, componentVerifyTicket);
    }
}
