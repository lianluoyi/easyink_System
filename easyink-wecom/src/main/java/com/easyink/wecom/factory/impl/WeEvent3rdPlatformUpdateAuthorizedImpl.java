package com.easyink.wecom.factory.impl;

import com.easyink.wecom.domain.vo.WeOpenXmlMessageVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.factory.WeOpenCallBackEventFactory;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 公众号/小程序对第三方平台授权更新通知
 * https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/2.0/api/Before_Develop/authorize_event.html
 *
 * @author wx
 * 2023/1/10 9:26
 **/
@Service("updateauthorized")
@RequiredArgsConstructor
@Slf4j
public class WeEvent3rdPlatformUpdateAuthorizedImpl implements WeOpenCallBackEventFactory {

    private final WechatOpenService wechatOpenService;

    @Override
    public void eventHandle(WeOpenXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getAuthorizationCode(), message.getAuthorizerAppid())) {
            log.info("[微信第三方平台-更新授权成功通知] 失败,参数缺失, message:{}", message);
            return;
        }
        wechatOpenService.addOrUpdate3rdConfig(message.getAuthorizationCode());
        log.info("[微信第三方平台-更新授权成功通知] 成功, AuthorizerAppid:{}, AuthorizationCode:{} ", message.getAuthorizerAppid(), message.getAuthorizationCode());
    }
}
