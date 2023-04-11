package com.easyink.wecom.handler.shorturl;

import com.alibaba.fastjson.JSON;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.ShortCodeType;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.RadarShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.service.radar.WeRadarOfficialAccountConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 类名: 雷达短链处理类
 *
 * @author : silver_chariot
 * @date : 2022/7/20 18:22
 **/
@Component(value = "radarShortUrlHandler")
@Slf4j
@RequiredArgsConstructor
public class RadarShortUrlHandler extends AbstractShortUrlHandler<RadarShortUrlAppendInfo> {

    private final WeRadarOfficialAccountConfigService weRadarOfficialAccountConfigService;

    @Override
    protected String handleByMapping(SysShortUrlMapping mapping) {
        return mapping.getLongUrl();
    }
    
    @Override
    public ShortUrlTypeEnum shortUrlType() {
        return ShortUrlTypeEnum.RADAR;
    }

    @Override
    public WeOpenConfig getWeOpenConfig(SysShortUrlMapping mapping) {
        String corpId = mapping.getRadarAppendInfo().getCorpId();
        return weRadarOfficialAccountConfigService.getRadarOfficialAccountConfig(corpId);
    }
}
