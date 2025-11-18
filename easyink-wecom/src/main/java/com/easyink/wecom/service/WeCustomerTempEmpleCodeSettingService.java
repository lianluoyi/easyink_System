package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSetting;

/**
 * 客户临时员工活码表(WeCustomerTempEmpleCodeSetting)表服务接口
 *
 * @author tigger
 * @since 2025-01-13 13:52:49
 */
public interface WeCustomerTempEmpleCodeSettingService extends IService<WeCustomerTempEmpleCodeSetting> {

    /**
     * 根据state获取客户专属活码设置
     *
     * @param state  state
     * @param corpId
     * @return
     */
    WeCustomerTempEmpleCodeSetting getByState(String state, String corpId);
}

