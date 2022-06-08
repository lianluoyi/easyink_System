package com.easywecom.wecom.factory.impl.customergroup;

import com.easywecom.common.constant.Constants;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 客户群创建事件
 * @date 2021/1/20 0:28
 **/
@Slf4j
@Component("create")
public class WeCallBackCreateGroupImpl extends WeEventStrategy {

    @Autowired
    private WeGroupService weGroupService;
    @Autowired
    private RedisCache redisCache;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getChatId())) {
            log.error("[create]客户群创建事件回调,接收参数缺失,message:{}", message);
            return;
        }
        if (!redisCache.addLock(message.getUniqueKey(message.getChatId()), "", Constants.CALLBACK_HANDLE_LOCK_TIME)) {
            log.info("[create]客户群创建事件回调,该回调已处理,不重复处理,message:{}", message);
            // 不重复处理
            return;
        }
        try {
            weGroupService.createWeGroup(message.getChatId(), message.getToUserName());
        } catch (Exception e) {
            log.error("[create]客户群创建事件回调,处理异常,message:{},ex:{}", message, ExceptionUtils.getStackTrace(e));
        }
    }
}
