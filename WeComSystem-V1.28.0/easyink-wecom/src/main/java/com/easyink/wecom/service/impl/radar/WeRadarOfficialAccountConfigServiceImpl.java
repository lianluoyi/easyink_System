package com.easyink.wecom.service.impl.radar;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.dto.radar.WeRadarOfficialAccountConfigDTO;
import com.easyink.wecom.domain.entity.radar.WeRadarOfficialAccountConfig;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.WeOpenConfigVO;
import com.easyink.wecom.domain.vo.WeServerTypeVO;
import com.easyink.wecom.mapper.radar.WeRadarOfficialAccountConfigMapper;
import com.easyink.wecom.service.We3rdAppService;
import com.easyink.wecom.service.radar.WeRadarOfficialAccountConfigService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Administrator
* @description 针对表【we_radar_official_account_config(雷达公众号配置)】的数据库操作Service实现
* @createDate 2023-01-11 16:40:39
*/
@Service
@RequiredArgsConstructor
public class WeRadarOfficialAccountConfigServiceImpl extends ServiceImpl<WeRadarOfficialAccountConfigMapper, WeRadarOfficialAccountConfig> implements WeRadarOfficialAccountConfigService{

    private final WeRadarOfficialAccountConfigMapper radarOfficialAccountConfigMapper;
    private final WechatOpenService wechatOpenService;
    private final We3rdAppService we3rdAppService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setRadarOfficialAccountConfig(WeRadarOfficialAccountConfigDTO dto, LoginUser loginUser) {
        if (dto == null || loginUser == null || StringUtils.isAnyBlank(loginUser.getCorpId(), dto.getAppId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 一个企业配置一个公众号
        if (this.getOne(new LambdaQueryWrapper<WeRadarOfficialAccountConfig>()
                .eq(WeRadarOfficialAccountConfig::getAppId, dto.getAppId())
                .eq(WeRadarOfficialAccountConfig::getCorpId, loginUser.getCorpId())) != null) {
            return;
        }
        radarOfficialAccountConfigMapper.delete(new LambdaQueryWrapper<WeRadarOfficialAccountConfig>().eq(WeRadarOfficialAccountConfig::getCorpId, loginUser.getCorpId()));
        WeRadarOfficialAccountConfig weRadarOfficialAccountConfig = dto.convertRadarOfficialAccountConfig();
        weRadarOfficialAccountConfig.setCorpId(loginUser.getCorpId());
        weRadarOfficialAccountConfig.setCreateBy(loginUser.getUserId());
        radarOfficialAccountConfigMapper.insert(weRadarOfficialAccountConfig);
    }

    @Override
    public WeOpenConfig getRadarOfficialAccountConfig(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        // 获取当前应用类型
        String serverType = we3rdAppService.getServerType().getServerType();
        if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
            return wechatOpenService.getOne(new LambdaQueryWrapper<WeOpenConfig>().eq(WeOpenConfig::getCorpId, corpId).last(GenConstants.LIMIT_1));
        }
        WeRadarOfficialAccountConfig weRadarOfficialAccountConfig = this.getOne(new LambdaQueryWrapper<WeRadarOfficialAccountConfig>().eq(WeRadarOfficialAccountConfig::getCorpId, corpId));
        if (weRadarOfficialAccountConfig == null || StringUtils.isBlank(weRadarOfficialAccountConfig.getAppId())) {
            return null;
        }
        return wechatOpenService.getConfig(corpId, weRadarOfficialAccountConfig.getAppId());
    }
}




