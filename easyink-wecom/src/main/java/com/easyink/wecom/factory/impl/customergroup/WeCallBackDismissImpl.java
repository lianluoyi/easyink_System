package com.easyink.wecom.factory.impl.customergroup;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 客户群解散事件
 * @date 2021/1/20 1:07
 **/
@Slf4j
@Component("dismiss")
public class WeCallBackDismissImpl extends WeEventStrategy {

    @Autowired
    private WeGroupService weGroupService;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        if (StringUtils.isAnyBlank(message.getToUserName(),message.getChatId())){
            log.error("客户遣散事件失败，返回数据内容为空：chatId：{}，corpId:{}", message.getChatId(), message.getToUserName());
            return;
        }
        try {
            weGroupService.deleteWeGroup(message.getChatId(), message.getToUserName());
        } catch (Exception e) {
            log.error("dismiss>>>>>>>>>param:{},ex:{}", message.getChatId(), ExceptionUtils.getStackTrace(e));
        }
    }
}
