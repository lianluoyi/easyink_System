package com.easywecom.wecom.factory.impl.customer;

import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.StaffActivateEnum;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeCustomerTransferRecordService;
import com.easywecom.wecom.service.WeResignedTransferRecordService;
import com.easywecom.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类名： WeCallbackTransferFailImpl
 *
 * @author 佚名
 * @description 客户接替失败事件
 * @date 2021/8/31 14:05
 */
@Slf4j
@Component("transfer_fail")
public class WeCallbackTransferFailImpl extends WeEventStrategy {
    @Autowired
    private WeUserService weUserService;
    @Autowired
    private WeCustomerTransferRecordService weCustomerTransferRecordService;
    @Autowired
    private WeResignedTransferRecordService weResignedTransferRecordService;


    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getUserId(), message.getExternalUserId())) {
            log.error("[transfer_fail]客户接替失败事件,收到的回调参数缺失,message:{}", message);
            return;
        }
        String userId = message.getUserId();
        String corpId = message.getToUserName();
        String externalUserId = message.getExternalUserId();
        String failReason = message.getFailReason();
        WeUser user = weUserService.selectWeUserById(corpId, userId);
        if (user == null) {
            log.info("[transfer_fail] 系统不存在该成员,message:{}", message);
            return;
        }
        // 是否是离职继承
        boolean isResigned = !StaffActivateEnum.ACTIVE.getCode().equals(user.getIsActivate());
        if (isResigned) {
            // 处理离职继承失败事件
            weResignedTransferRecordService.handleTransferFail(corpId, userId, externalUserId, failReason);
        } else {
            // 处理在职继承失败事件
            weCustomerTransferRecordService.handleTransferFail(corpId, userId, externalUserId, failReason);
        }
    }


}
