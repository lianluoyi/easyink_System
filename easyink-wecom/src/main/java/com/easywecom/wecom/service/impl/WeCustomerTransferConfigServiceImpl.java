package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.entity.transfer.WeCustomerTransferConfig;
import com.easywecom.wecom.mapper.WeCustomerTransferConfigMapper;
import com.easywecom.wecom.service.WeCustomerTransferConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * 类名: 继承设置表业务层接口实现类
 *
 * @author : silver_chariot
 * @date : 2021/12/1 10:53
 */
@Service
@Slf4j
public class WeCustomerTransferConfigServiceImpl extends ServiceImpl<WeCustomerTransferConfigMapper, WeCustomerTransferConfig> implements WeCustomerTransferConfigService {

    private final WeCustomerTransferConfigMapper weCustomerTransferConfigMapper;

    @Autowired
    public WeCustomerTransferConfigServiceImpl(@NotNull WeCustomerTransferConfigMapper weCustomerTransferConfigMapper) {
        this.weCustomerTransferConfigMapper = weCustomerTransferConfigMapper;
    }

    @Override
    public void editConfig(WeCustomerTransferConfig config) {
        if (StringUtils.isBlank(config.getCorpId()) || config.getEnableTransferInfo() == null || config.getEnableSideBar() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        weCustomerTransferConfigMapper.updateById(config);
    }

    @Override
    public Boolean initTransferConfig(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return false;
        }
        WeCustomerTransferConfig config = WeCustomerTransferConfig.builder()
                .corpId(corpId)
                .enableSideBar(Boolean.FALSE)
                .enableTransferInfo(Boolean.TRUE)
                .build();
        try {
            this.saveOrUpdate(config);
        } catch (Exception e) {
            log.error("初始化继承设置异常,corpId:{},E:{}", corpId, ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
    }
}
