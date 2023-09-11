package com.easyink.wecom.factory.impl.customeracquisition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.vo.WeEmpleCodeWarnConfigVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.mapper.WeUserMapper;
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
 * @description 获客链接不可用事件
 * @date 2023/8/27 18:12
 */
@Slf4j
@Component("link_unavailable")
public class WeCallBackLinkUnavailableImpl extends WeEventStrategy {

    private final WeEmpleCodeWarnConfigService weEmpleCodeWarnConfigService;

    private final CustomerAssistantRedisCache customerAssistantRedisCache;

    private final WeEmpleCodeMapper weEmpleCodeMapper;

    private final CustomerAssistantService customerAssistantService;
    private final WeUserMapper weUserMapper;

    public WeCallBackLinkUnavailableImpl(WeEmpleCodeWarnConfigService weEmpleCodeWarnConfigService, CustomerAssistantRedisCache customerAssistantRedisCache, WeEmpleCodeMapper weEmpleCodeMapper, CustomerAssistantService customerAssistantService, WeUserMapper weUserMapper) {
        this.weEmpleCodeWarnConfigService = weEmpleCodeWarnConfigService;
        this.customerAssistantRedisCache = customerAssistantRedisCache;
        this.weEmpleCodeMapper = weEmpleCodeMapper;
        this.customerAssistantService = customerAssistantService;
        this.weUserMapper = weUserMapper;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (!checkParams(message)) {
            return;
        }
        String corpId = message.getToUserName();
        String today = DateUtils.dateTime(new Date());
        WeEmpleCodeWarnConfigVO config = weEmpleCodeWarnConfigService.getConfig(corpId);
        if (config == null) {
            log.info("[link_unavailable] 未获取到企业告警配置信息，corpId:{}", corpId);
            return;
        }
        WeEmpleCode weEmpleCode = weEmpleCodeMapper.getAssistantByLinkId(message.getLinkId(), corpId);
        if (weEmpleCode == null) {
            log.info("[link_unavailable] 未获取到当前链接信息，corpId:{}", corpId);
            return;
        }
        // 若告警通知打开，则开始告警
        if (!config.getLinkUnavailableSwitch()) {
            log.info("[link_unavailable] 未开启链接不可用告警开关，停止告警，corpId:{}", corpId);
            return;
        }
        // 若告警设置为每天仅告警一次，且接收回调当天时间等于缓存中的时间值，表示已经进行过通知，不继续处理
        if (config.getType().equals(CustomerAssistantConstants.ONLY_ONE_WARN_TYPE) && today.equals(customerAssistantRedisCache.getLinkUnavailableValue(corpId))) {
            log.info("[link_unavailable] 今日已进行过告警通知，不再继续告警，corpId:{}", corpId);
            return;
        }
        try {
            // 获取通知员工
            String sendUser = getSendUser(config, weEmpleCode);
            if (StringUtils.isBlank(sendUser)) {
                log.info("[link_unavailable]:没有需要通知的员工，corpId:{}, sendUser:{}", config.getCorpId(), sendUser);
                return;
            }
            // 获取发送内容
            String content = CustomerAssistantConstants.LINK_UNAVAILABLE_NOTICE.replace(CustomerAssistantConstants.CUSTOMER_ASSISTANT_LINK_NAME, weEmpleCode.getScenario());
            // 发送应用消息
            customerAssistantService.sendToUser(corpId, sendUser, content);
        } catch (Exception e) {
            log.error("[link_unavailable] 发送应用消息通知出现异常，corpId:{}，异常原因ex:{}", corpId, ExceptionUtils.getStackTrace(e));
        }
        // 更新Redis值
        customerAssistantRedisCache.setLinkUnavailableValue(corpId, today);
    }

    /**
     * 校验回调格式
     *
     * @param message {@link WxCpXmlMessageVO}
     * @return 正确：true，错误：false
     */
    private boolean checkParams(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getLinkId())) {
            log.error("[link_unavailable]:回调数据不完整,message:{}", message);
            return false;
        }
        return true;
    }

    /**
     * 获取通知员工
     *
     * @param config {@link WeEmpleCodeWarnConfigVO}
     * @param weEmpleCode {@link WeEmpleCode}
     * @return 通知员工，使用"|"隔开
     */
    private String getSendUser(WeEmpleCodeWarnConfigVO config, WeEmpleCode weEmpleCode) {
        String sendUser = StringUtils.EMPTY;
        // 如果通知其他人开关关闭
        if (config.getAlarmOtherUser()) {
            sendUser = sendUser + (config.getLinkUnavailableUsers().replace(WeConstans.COMMA, WeConstans.VERTICAL_BAR));
        }
        // 如果告警通知创建人关闭，不对连接创建人进行通知
        if (!config.getAlarmCreater()) {
            return sendUser;
        }
        // 如果链接创建人不是admin，添加到通知列表
        if (!Constants.SUPER_ADMIN.equals(weEmpleCode.getCreateBy())) {
            WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>().eq(WeUser::getName, weEmpleCode.getCreateBy()).eq(WeUser::getCorpId, config.getCorpId()));
            if (weUser != null) {
                sendUser = sendUser + WeConstans.VERTICAL_BAR + weUser.getUserId();
            }
        }
        return sendUser;
    }
}
