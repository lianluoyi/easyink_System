package com.easyink.wecom.factory.impl;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 类名: WeInfoTypeSuiteTicketImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 14:14
 */
@Slf4j
@Service("suite_ticket")
public class WeInfoTypeSuiteTicketImpl implements WeCallBackEventFactory {

    private final RedisCache redisCache;

    @Autowired
    public WeInfoTypeSuiteTicketImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (ObjectUtils.isEmpty(message)) {
            log.error("message为空");
            return;
        }
        if (StringUtils.isNoneBlank(message.getSuiteId(), message.getSuiteTicket())) {
            String redisKey = WeConstans.WE_SUITE_TICKET + message.getSuiteId();
            redisCache.setCacheObject(redisKey, message.getSuiteTicket(), 30, TimeUnit.MINUTES);
        }
        log.info("服务器推送的suiteId:{},suiteTicket:{}", message.getSuiteId(), message.getSuiteTicket());
    }
}
