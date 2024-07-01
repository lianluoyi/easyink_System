package com.easyink.wecom.publishevent;

import com.easyink.wecom.utils.ApplicationMessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 发送App应用通知时间
 * @author tigger
 * 2024/2/19 16:06
 **/
@Slf4j
@Component
@AllArgsConstructor
public class SendAppMessageEventListener {

    private final ApplicationMessageUtil applicationMessageUtil;

    /**
     * 处理发送app应用通知监听方法
     * @param event 事件
     */
    @TransactionalEventListener()
    public void sendAppMessage(SendAppMessageEvent event) {
        try {
            applicationMessageUtil.sendAppMessage(event.getUserIdList(), event.getCorpId(), event.getMsgContentTemplate(),
                    event.getMsgParams());
        } catch (Exception e) {
            log.error("[发送应用app消息] 发送异常: {}", ExceptionUtils.getStackTrace(e));
        }
    }
}
