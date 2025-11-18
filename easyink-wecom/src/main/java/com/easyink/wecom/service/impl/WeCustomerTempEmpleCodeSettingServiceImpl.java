package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.mapper.WeCustomerTempEmpleCodeSettingMapper;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSetting;
import com.easyink.wecom.service.WeCustomerTempEmpleCodeSettingService;
import org.springframework.stereotype.Service;

/**
 * 客户临时员工活码表(WeCustomerTempEmpleCodeSetting)表服务实现类
 *
 * @author tigger
 * @since 2025-01-13 13:52:49
 */
@Service("weCustomerTempEmpleCodeSettingService")
public class WeCustomerTempEmpleCodeSettingServiceImpl extends ServiceImpl<WeCustomerTempEmpleCodeSettingMapper, WeCustomerTempEmpleCodeSetting> implements WeCustomerTempEmpleCodeSettingService {

    @Override
    public WeCustomerTempEmpleCodeSetting getByState(String state, String corpId) {
        if(StringUtils.isAnyBlank(state, corpId)){
            return null;
        }
        return this.baseMapper.selectByState(state, corpId);
    }
}

