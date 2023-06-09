package com.easyink.wecom.listener;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easyink.common.constant.WeConstans;
import com.easyink.wecom.domain.WeGroupCodeActual;
import com.easyink.wecom.service.WeGroupCodeActualService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * 类名： 过期群码监听
 *
 * @author 佚名
 * @date 2021/11/9 20:28
 */
@Slf4j
@Component
public class ActualGroupCodeExpiredListener extends KeyExpirationEventMessageListener {
    @Autowired
    private WeGroupCodeActualService weGroupCodeActualService;

    public ActualGroupCodeExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (message == null) {
            return;
        }
        String messageStr = message.toString();
        if (StringUtils.isBlank(messageStr)) {
            return;
        }

        if (messageStr.contains(WeConstans.WE_ACTUAL_GROUP_CODE_KEY)) {
            String[] keyArr = messageStr.split(":");
            String actualId;
            if (keyArr.length <= 1) {
                log.info("ActualGroupCodeExpiredListener messageKey={}", messageStr);
                return;
            } else if (keyArr.length == 2) {
                //we_actual_group_code_key:{actualId}
                actualId = keyArr[1];
                weGroupCodeActualService.update(new LambdaUpdateWrapper<WeGroupCodeActual>().eq(WeGroupCodeActual::getId, actualId).set(WeGroupCodeActual::getStatus, WeConstans.WE_CUSTOMER_MSG_RESULT_DEFALE));
                log.warn("客户群活码过期,messageStr:{},actualId:{}", messageStr, actualId);
            }else {
                log.warn("客户群活码过期,messageStr:{}", messageStr);
            }
        }
    }
}