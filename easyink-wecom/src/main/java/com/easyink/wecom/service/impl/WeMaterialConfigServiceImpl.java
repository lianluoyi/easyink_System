package com.easyink.wecom.service.impl;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeMaterialConfig;
import com.easyink.wecom.mapper.WeMaterialConfigMapper;
import com.easyink.wecom.service.WeMaterialConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * 类名：WeMaterialConfigServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-10-11
 */
@Slf4j
@Service
public class WeMaterialConfigServiceImpl implements WeMaterialConfigService {
    private final WeMaterialConfigMapper weMaterialConfigMapper;

    @Autowired
    public WeMaterialConfigServiceImpl(@NotNull WeMaterialConfigMapper weMaterialConfigMapper) {
        this.weMaterialConfigMapper = weMaterialConfigMapper;
    }

    @Override
    public int update(WeMaterialConfig weMaterialConfig) {
        if (StringUtils.isBlank(weMaterialConfig.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return weMaterialConfigMapper.update(weMaterialConfig);
    }

    @Override
    public int insert(WeMaterialConfig weMaterialConfig) {
        if (StringUtils.isBlank(weMaterialConfig.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return weMaterialConfigMapper.insert(weMaterialConfig);
    }

    @Override
    public WeMaterialConfig findByCorpId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeMaterialConfig weMaterialConfig = weMaterialConfigMapper.findByCorpId(corpId);
        //不存在配置，则初始化数据
        if (weMaterialConfig == null) {
            weMaterialConfig = new WeMaterialConfig();
            weMaterialConfig.setCorpId(corpId);
            weMaterialConfig.setIsDel(WeConstans.DEFAULT_WE_MATERIAL_NOT_DEL);
            weMaterialConfig.setDelDays(WeConstans.DEFAULT_WE_MATERIAL_DEL_DAYS);
            this.insert(weMaterialConfig);
        }
        return weMaterialConfig;
    }


}
