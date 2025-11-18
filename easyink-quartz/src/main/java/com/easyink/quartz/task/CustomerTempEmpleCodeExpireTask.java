package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeExternalContactClient;
import com.easyink.wecom.domain.dto.WeExternalContactDTO;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSetting;
import com.easyink.wecom.mapper.WeCustomerTempEmpleCodeSettingMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 清除过期专属活码定时任务
 *
 * @author tigger
 * @date 2025/1/15 10:39
 */
@Slf4j
@Component("customerTempEmpleCodeExpireTask")
@AllArgsConstructor
public class CustomerTempEmpleCodeExpireTask {

    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerTempEmpleCodeSettingMapper weCustomerTempEmpleCodeSettingMapper;
    private final WeExternalContactClient weExternalContactClient;

    public void deleteExpireCustomerTempEmpleCode() {
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    Date now = new Date();
                    // 获取小于过期时间点的数据
                    List<WeCustomerTempEmpleCodeSetting> deleteList = weCustomerTempEmpleCodeSettingMapper.selectExpireByTime(now, weCorpAccount.getCorpId());
                    for (WeCustomerTempEmpleCodeSetting customerTempEmpleCodeSetting : deleteList) {
                        WeExternalContactDTO.WeContactWay weContactWay = new WeExternalContactDTO.WeContactWay();
                        weContactWay.setConfig_id(customerTempEmpleCodeSetting.getConfigId());
                        WeExternalContactDTO weExternalContactDTO = weExternalContactClient.delContactWay(weContactWay, weCorpAccount.getCorpId());
                        if (weExternalContactDTO.isFail()) {
                            log.info("[专属活码] 清除过期专属活码失败, corpId:{}, configId:{}, id:{}", weCorpAccount.getCorpId(), customerTempEmpleCodeSetting.getConfigId(), customerTempEmpleCodeSetting.getId());
                            continue;
                        }
                        weCustomerTempEmpleCodeSettingMapper.deleteById(customerTempEmpleCodeSetting.getId());
                    }
                }
            } catch (Exception e) {
                log.info("[专属活码] 清除过期专属活码定时任务执行异常, corpId:{}, ex:{}", weCorpAccount.getCorpId(), ExceptionUtils.getStackTrace(e));
            }
        });
    }
}
