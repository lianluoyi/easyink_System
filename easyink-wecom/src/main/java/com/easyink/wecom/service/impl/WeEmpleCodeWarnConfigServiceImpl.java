package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.WeEmpleCodeWarnConfig;
import com.easyink.wecom.domain.dto.emplecode.EmpleWarnConfigDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeWarnConfigVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeEmpleCodeWarnConfigMapper;
import com.easyink.wecom.service.WeEmpleCodeWarnConfigService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 获客链接告警设置Service业务层
 *
 * @author lichaoyu
 * @date 2023/8/23 9:39
 */
@Slf4j
@Service
public class WeEmpleCodeWarnConfigServiceImpl extends ServiceImpl<WeEmpleCodeWarnConfigMapper, WeEmpleCodeWarnConfig> implements WeEmpleCodeWarnConfigService {


    private final WeUserService weUserService;

    public WeEmpleCodeWarnConfigServiceImpl(WeUserService weUserService) {
        this.weUserService = weUserService;
    }

    @Override
    public Integer saveOrUpdateConfig(EmpleWarnConfigDTO warnConfigDTO) {
        if (StringUtils.isBlank(warnConfigDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeEmpleCodeWarnConfig weEmpleCodeWarnConfig = new WeEmpleCodeWarnConfig();
        BeanUtils.copyProperties(warnConfigDTO, weEmpleCodeWarnConfig);
        weEmpleCodeWarnConfig.setUpdateBy(LoginTokenService.getUsername());
        weEmpleCodeWarnConfig.setUpdateTime(new Date());
        return this.baseMapper.saveOrUpdateConfig(weEmpleCodeWarnConfig);
    }

    /**
     * 获取获客链接告警配置信息
     *
     * @param corpId 企业ID
     * @return {@link WeEmpleCodeWarnConfig}
     */
    @Override
    public WeEmpleCodeWarnConfigVO getConfig(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeEmpleCodeWarnConfigVO config = this.baseMapper.getConfig(corpId);
        if (config == null) {
            return new WeEmpleCodeWarnConfigVO(corpId);
        }
        // 获取存在链接不可用通知员工信息
        if (config.getLinkUnavailableUsers() != null) {
            config.setLinkUnavailableUsersInfo(weUserService.getUserInfo(config.getLinkUnavailableUsers(), corpId));
        }
        // 获取链接即将耗尽通知员工信息
        if (config.getBalanceLowUsers() != null) {
            config.setBalanceLowUsersInfo(weUserService.getUserInfo(config.getBalanceLowUsers(), corpId));
        }
        // 获取链接已耗尽通知员工信息
        if (config.getBalanceExhaustedUsers() != null) {
            config.setBalanceExhaustedUsersInfo(weUserService.getUserInfo(config.getBalanceExhaustedUsers(), corpId));
        }
        // 获取获客额度即将过期通知员工信息
        if (config.getQuotaExpireSoonUsers() != null) {
            config.setQuotaExpireSoonUsersInfo(weUserService.getUserInfo(config.getQuotaExpireSoonUsers(), corpId));
        }
        return config;
    }

}
