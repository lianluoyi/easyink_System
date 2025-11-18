package com.easyink.wecom.domain.model.emplecode;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.constant.emple.EmployCodeConstants;
import lombok.Data;
import lombok.Value;

/**
 * 企微回调state 值对象
 * @author tigger
 * 2025/1/14 10:29
 **/
@Data
@Value(staticConstructor = "valueOf")
public class State {
    /**
     * 回到state
     */
    String state;

    /**
     * 判断是否是客户专属活码添加的state
     * @return true 是，false 不是
     */
    public boolean isCustomerEmploy() {
        return state.startsWith(EmployCodeConstants.CUSTOMER_EMPLOY_STATE_PREFIX);
    }

    /**
     * 客户专属活码state解包出活码id
     * @return true 是，false 不是
     */
    public Long unWrapCustomerEmployCodeId() {
        return Long.valueOf(this.state.replace(EmployCodeConstants.CUSTOMER_EMPLOY_STATE_PREFIX, ""));
    }

    /**
     * 是否客户助手回调state
     * @return true 是，false 不是
     */
    public boolean isAssistantState() {
        return this.state.startsWith(CustomerAssistantConstants.STATE_PREFIX);
    }

    /**
     * 是否任务裂变
     * @return true 是，false 不是
     */
    public boolean isFission() {
        return this.state.contains(WeConstans.FISSION_PREFIX);
    }
}
