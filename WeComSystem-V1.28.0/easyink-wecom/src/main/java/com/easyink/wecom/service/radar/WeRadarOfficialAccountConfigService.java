package com.easyink.wecom.service.radar;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.dto.radar.WeRadarOfficialAccountConfigDTO;
import com.easyink.wecom.domain.entity.radar.WeRadarOfficialAccountConfig;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.WeOpenConfigVO;

/**
* @author Administrator
* @description 针对表【we_radar_official_account_config(雷达公众号配置)】的数据库操作Service
* @createDate 2023-01-11 16:40:39
*/
public interface WeRadarOfficialAccountConfigService extends IService<WeRadarOfficialAccountConfig> {

    /**
     * 配置雷达公众号
     *
     * @param dto       {@link WeRadarOfficialAccountConfigDTO}
     * @param loginUser {@link LoginUser}
     */
    void setRadarOfficialAccountConfig(WeRadarOfficialAccountConfigDTO dto, LoginUser loginUser);


    /**
     * 获取雷达公众号配置
     *
     * @param corpId    企业id
     * @return
     */
    WeOpenConfig getRadarOfficialAccountConfig(String corpId);
}
