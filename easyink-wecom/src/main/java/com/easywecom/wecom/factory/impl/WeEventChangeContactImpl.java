package com.easywecom.wecom.factory.impl;

import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeCallBackEventFactory;
import com.easywecom.wecom.factory.WeStrategyBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名： 企微变更事件通知处理类
 *
 * @author 佚名
 * @date 2021/8/26 20:32
 */
@Service("change_contact")
@Slf4j
public class WeEventChangeContactImpl implements WeCallBackEventFactory {
    private final WeStrategyBeanFactory weStrategyBeanFactory;

    @Autowired
    public WeEventChangeContactImpl(WeStrategyBeanFactory weStrategyBeanFactory) {
        this.weStrategyBeanFactory = weStrategyBeanFactory;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {


        //新增: create_user 更新: update_user 删除:delete_user
        String changeType = message.getChangeType();
        log.info("回调成员事件通知,通知类型：{},企业ID：{}", changeType, message.getToUserName());
        if (StringUtils.isNotBlank(changeType)) {
            weStrategyBeanFactory.getResource(changeType, message);
        }
    }
}
