package com.easyink.wecom.factory.impl.customeracquisition;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.vo.WeEmpleCodeWarnConfigVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.CustomerAssistantService;
import com.easyink.wecom.service.WeEmpleCodeWarnConfigService;
import com.easyink.wecom.utils.redis.CustomerAssistantRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lichaoyu
 * @description 获客额度即将过期事件
 * @date 2023/9/1 16:34
 */
@Slf4j
@Component("quota_expire_soon")
public class WeCallBackQuotaExpireSoonImpl extends WeEventStrategy {

    private final WeEmpleCodeWarnConfigService weEmpleCodeWarnConfigService;

    private final CustomerAssistantRedisCache customerAssistantRedisCache;

    private final CustomerAssistantService customerAssistantService;

    public WeCallBackQuotaExpireSoonImpl(WeEmpleCodeWarnConfigService weEmpleCodeWarnConfigService, CustomerAssistantRedisCache customerAssistantRedisCache, CustomerAssistantService customerAssistantService) {
        this.weEmpleCodeWarnConfigService = weEmpleCodeWarnConfigService;
        this.customerAssistantRedisCache = customerAssistantRedisCache;
        this.customerAssistantService = customerAssistantService;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (!checkParams(message)) {
            return;
        }
        String corpId = message.getToUserName();
        String today = DateUtils.dateTime(new Date());
        Integer expireQuotaNum = message.getExpireQuotaNum();
        // 企微返回的时间戳是北京时间格式， 需乘以1000转换为格林威治时间（GMT）
        String expireTime = DateUtils.getDateTime(new Date(message.getExpireTime() * 1000));
        WeEmpleCodeWarnConfigVO config = weEmpleCodeWarnConfigService.getConfig(corpId);
        if (config == null) {
            log.info("[quota_expire_soon] 未获取到企业告警配置信息，corpId:{}", corpId);
            return;
        }
        // 若获客额度即将过期告警通知关闭，停止告警
        if (!config.getQuotaExpireSoonSwitch()) {
            log.info("[quota_expire_soon] 未开启获客额度即将过期告警开关，停止告警，corpId:{}", corpId);
            return;
        }
        // 若告警设置为每天仅告警一次，且接收回调当天时间等于缓存中的时间值，表示已经进行过通知，不继续处理
        if (config.getType().equals(CustomerAssistantConstants.ONLY_ONE_WARN_TYPE) && today.equals(customerAssistantRedisCache.getQuotaExpireSoonValue(corpId))) {
            log.info("[quota_expire_soon] 今日已进行过告警通知，不再继续告警，corpId:{}", corpId);
            return;
        }
        // 若开关打开，但未设置告警通知员工，则不进行告警
        if (StringUtils.isBlank(config.getQuotaExpireSoonUsers())) {
            log.info("[quota_expire_soon] 未设置告警通知员工，不再继续告警，corpId:{}", corpId);
            return;
        }
        try {
            // 获取发送内容
            String content = CustomerAssistantConstants.QUOTA_EXPIRE_SOON_NOTICE
                    .replace(CustomerAssistantConstants.EXPIRE_QUOTA_NUM, String.valueOf(expireQuotaNum))
                    .replace(CustomerAssistantConstants.EXPIRE_TIME, expireTime);
            // 发送应用消息
            customerAssistantService.sendToUser(config.getCorpId(), config.getQuotaExpireSoonUsers().replace(WeConstans.COMMA, WeConstans.VERTICAL_BAR), content);
        } catch (Exception e) {
            log.error("[quota_expire_soon] 发送应用消息通知出现异常，corpId:{}，异常原因ex:{}", corpId, ExceptionUtils.getStackTrace(e));
        }
        // 更新Redis值
        customerAssistantRedisCache.setQuotaExpireSoonValue(corpId, today);
    }

    /**
     * 校验回调格式
     *
     * @param message {@link WxCpXmlMessageVO}
     * @return 正确：true，错误：false
     */
    private boolean checkParams(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName()) || message.getExpireQuotaNum() == null || message.getExpireTime() == null) {
            log.error("[quota_expire_soon]:回调数据不完整,message:{}", message);
            return false;
        }
        return true;
    }
}
