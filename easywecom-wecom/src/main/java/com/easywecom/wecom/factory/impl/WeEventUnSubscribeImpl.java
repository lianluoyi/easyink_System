package com.easywecom.wecom.factory.impl;

import com.alibaba.fastjson.JSON;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeCallBackEventFactory;
import com.easywecom.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: WeEventSubscribeImpl
 *
 * @author: 1*+
 * @date: 2021-12-29 13:56
 */
@Service("unsubscribe")
@Slf4j
public class WeEventUnSubscribeImpl implements WeCallBackEventFactory {

    private final WeUserService weUserService;
    private final RuoYiConfig ruoYiConfig;

    public WeEventUnSubscribeImpl(WeUserService weUserService, RuoYiConfig ruoYiConfig) {
        this.weUserService = weUserService;
        this.ruoYiConfig = ruoYiConfig;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        log.info("取消订阅消息:{}", JSON.toJSONString(message));
        if (ruoYiConfig.isInternalServer()) {
            // 删除员工
            List<String> userIds = new ArrayList<>();
            userIds.add(message.getFromUserName());
            weUserService.deleteUsersNoToWeCom(message.getToUserName(), userIds);
        }
    }
}
