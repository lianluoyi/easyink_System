package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeLockSidebarConfig;
import com.easyink.wecom.domain.dto.LockSidebarConfigDTO;
import com.easyink.wecom.domain.vo.WeLockSidebarConfigVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeLockSidebarConfigMapper;
import com.easyink.wecom.service.WeLockSidebarConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 第三方SCRM系统侧边栏配置(WeLockSidebarConfig)表服务实现类
 *
 * @author wx
 * @since 2023-03-14 15:39:09
 */
@Service("weLockSidebarConfigService")
@RequiredArgsConstructor
public class WeLockSidebarConfigServiceImpl extends ServiceImpl<WeLockSidebarConfigMapper, WeLockSidebarConfig> implements WeLockSidebarConfigService {

    private final WeLockSidebarConfigMapper weLockSidebarConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(LockSidebarConfigDTO dto) {
        if (dto == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        weLockSidebarConfigMapper.delete(new LambdaUpdateWrapper<WeLockSidebarConfig>().eq(WeLockSidebarConfig::getCorpId, corpId));
        weLockSidebarConfigMapper.insert(new WeLockSidebarConfig(dto.getAppId(), corpId, dto.getAppSecret()));
    }

    @Override
    public WeLockSidebarConfigVO getConfig(String appId) {
        WeLockSidebarConfig config = weLockSidebarConfigMapper.selectOne(new LambdaQueryWrapper<WeLockSidebarConfig>().eq(WeLockSidebarConfig::getAppId, appId).last(GenConstants.LIMIT_1));
        if (config == null || StringUtils.isBlank(config.getAppSecret())) {
            throw new CustomException(ResultTip.TIP_LOCK_CONFIG_MISSING);
        }
        return new WeLockSidebarConfigVO(config);
    }
}
