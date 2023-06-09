package com.easyink.wecom.factory.impl;

import com.easyink.common.config.WechatOpenConfig;
import com.easyink.wecom.domain.vo.WeOpenXmlMessageVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.factory.WeOpenCallBackEventFactory;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 公众号/小程序对第三方平台授权成功通知
 * https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/2.0/api/Before_Develop/authorize_event.html
 *
 * @author wx
 * 2023/1/9 16:46
 **/
@Service("authorized")
@RequiredArgsConstructor
@Slf4j
public class WeEvent3rdPlatformAuthorizedImpl implements WeOpenCallBackEventFactory {

    @Override
    public void eventHandle(WeOpenXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getAuthorizationCode(), message.getAuthorizerAppid())) {
            log.info("[微信第三方平台-授权成功通知] 失败,参数缺失, message:{}", message);
            return;
        }
        log.info("[微信第三方平台-授权成功通知] 成功, AuthorizerAppid:{}, AuthorizationCode:{} ", message.getAuthorizerAppid(), message.getAuthorizationCode());
    }
}
