package com.easyink.wecom.publishevent.welcomemsg;

import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.model.emplecode.State;
import com.easyink.wecom.domain.vo.EmplyCodeWelcomeMsgInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送欢迎语成功事件
 *
 * @author tigger
 * 2025/8/26 17:04
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendWelcomeMsgSuccessEvent {
    /**
     * 客户id
     */
    private CustomerId customerId;
    /**
     * 活码欢迎语信息
     */
    private EmplyCodeWelcomeMsgInfo emplyCodeWelcomeMsgInfo;

    /**
     * 添加好友sate
     */
    private State originState;
}
