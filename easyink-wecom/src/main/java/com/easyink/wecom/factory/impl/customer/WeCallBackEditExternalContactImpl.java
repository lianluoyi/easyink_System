package com.easyink.wecom.factory.impl.customer;

import com.easyink.common.redis.CustomerRedisCache;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * 类名: 客户编辑事件回调处理
 *
 * @author: 1*+
 * @date: 2021-08-04 14:02
 */
@Slf4j
@Component("edit_external_contact")
public class WeCallBackEditExternalContactImpl extends WeEventStrategy {

    private final WeCustomerService weCustomerService;
    @Resource(name = "customerRedisCache")
    private CustomerRedisCache customerRedisCache;


    @Autowired
    public WeCallBackEditExternalContactImpl(@NotNull WeCustomerService weCustomerService) {
        this.weCustomerService = weCustomerService;
    }

    /**
     * 先缓存 客户信息,后续由 {@link SyncCustomerChangeTask} 执行
     *
     * @param message
     */
    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getExternalUserId(), message.getUserId(), message.getToUserName())) {
            log.error("edit_external_contact:回调消息不完整,message:{}", message);
            return;
        }
        //  存入redis 后续由定时任务统一处理
        customerRedisCache.saveCallback(message.getToUserName(), message.getUserId(), message.getExternalUserId());
//        weCustomerService.updateExternalContactV2(message.getToUserName(), message.getUserId(), message.getExternalUserId());
    }
}
