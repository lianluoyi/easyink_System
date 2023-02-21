package com.easyink.wecom.domain.dto.radar;

import com.baomidou.mybatisplus.annotation.TableId;
import com.easyink.wecom.domain.entity.radar.WeRadarOfficialAccountConfig;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 雷达公众号配置
 *
 * @author wx
 * 2023/1/11 16:55
 **/
@Data
public class WeRadarOfficialAccountConfigDTO {
    /**
     * 企业id
     */
    private String corpId;

    /**
     * 公众号appid
     */
    @NotBlank(message = "公众号appid不得为空")
    private String appId;

    /**
     * 转化为WeRadarOfficialAccountConfig实体
     */
    public WeRadarOfficialAccountConfig convertRadarOfficialAccountConfig() {
        WeRadarOfficialAccountConfig weRadarOfficialAccountConfig = new WeRadarOfficialAccountConfig();
        weRadarOfficialAccountConfig.setAppId(appId);
        return weRadarOfficialAccountConfig;
    }
}
