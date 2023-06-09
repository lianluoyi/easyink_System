package com.easyink.wecom.listener;


import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.client.WeExternalContactClient;
import com.easyink.wecom.domain.dto.WeExternalContactDTO;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description redis 键值过期监听
 * @date 2021/2/8 17:24
 **/
@Slf4j
@Component
public class EmpleCodeExpiredListener extends KeyExpirationEventMessageListener {
    @Autowired
    private WeExternalContactClient weExternalContactClient;
    @Autowired
    private WeUserService weUserService;

    public EmpleCodeExpiredListener(RedisMessageListenerContainer listenerContainer) {
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

        if (messageStr.contains(WeConstans.WE_EMPLE_CODE_KEY)) {
            String[] keyArr = messageStr.split(":");
            String corpId = null;
            String configId;
            if (keyArr.length <= 1) {
                log.info("EmpleCodeExpiredListener messageKey={}", messageStr);
                return;
            } else if (keyArr.length > 2) {
                //we_emple_code_key:{corpId}:{configId}
                corpId = keyArr[1];
                configId = keyArr[2];
            } else {
                //we_emple_code_key:{configId} (原始key)
                configId = keyArr[1];
            }
            if (StringUtils.isNotBlank(corpId) && StringUtils.isNotBlank(configId)) {
                WeExternalContactDTO.WeContactWay weContactWay = new WeExternalContactDTO.WeContactWay();
                weContactWay.setConfig_id(configId);
                weExternalContactClient.delContactWay(weContactWay, corpId);
            } else {
                log.warn("员工活码过期,messageStr:{},configId:{}", messageStr, configId);
            }
        } else if (messageStr.contains(RedisKeyConstants.DELETE_USER_KEY)) {
            String[] arr = messageStr.split(":");
            int totalLength = 2;
            if (ArrayUtil.isNotEmpty(arr) && arr.length >= totalLength) {
                // 截取corpId
                int corpIdIndex = 1;
                String corpId = arr[corpIdIndex];
                if (StringUtils.isNotBlank(corpId)) {
                    // 同步对应公司id的离职员工
                    Map<String, String> userIdInDbMap = weUserService.list(new LambdaQueryWrapper<WeUser>().select(WeUser::getUserId).eq(WeUser::getCorpId, corpId))
                            .stream().collect(Collectors.toMap(WeUser::getUserId, WeUser::getUserId));
                    weUserService.syncWeLeaveUserV2(corpId, userIdInDbMap);
                }
            }
        }
    }
}
