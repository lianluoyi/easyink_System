package com.easyink.wecom.factory.impl.customeracquisition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author lichaoyu
 * @date 2023/8/29 21:14
 */
@Slf4j
@Component("delete_link")
public class WeCallBackDeleteLinkImpl extends WeEventStrategy {

    private final WeEmpleCodeMapper weEmpleCodeMapper;

    public WeCallBackDeleteLinkImpl(WeEmpleCodeMapper weEmpleCodeMapper) {
        this.weEmpleCodeMapper = weEmpleCodeMapper;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (!checkParams(message)) {
            return;
        }
        String corpId = message.getToUserName();
        weEmpleCodeMapper.removeAssistantByLinkId(message.getLinkId(), corpId);
        log.info("[delete_link] 更新企微后台删除的获客链接状态,corpId:{},linkId:{}", corpId, message.getLinkId());
    }

    /**
     * 校验回调格式
     *
     * @param message {@link WxCpXmlMessageVO}
     * @return 正确：true，错误：false
     */
    private boolean checkParams(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getLinkId())) {
            log.error("[delete_link]:回调数据不完整,message:{}", message);
            return false;
        }
        return true;
    }
}
