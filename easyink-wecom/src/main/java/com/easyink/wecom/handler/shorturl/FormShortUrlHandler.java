package com.easyink.wecom.handler.shorturl;

import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.FormShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 表单短链处理类
 *
 * @author wx
 * 2023/3/16 11:14
 **/
@Component(value = "formShortUrlHandler")
@Slf4j
@RequiredArgsConstructor
public class FormShortUrlHandler extends AbstractShortUrlHandler<FormShortUrlAppendInfo> {

    private final WechatOpenService wechatOpenService;

    @Override
    protected String handleByMapping(SysShortUrlMapping mapping) {
        return mapping.getLongUrl();
    }

    @Override
    public ShortUrlTypeEnum shortUrlType() {
        return ShortUrlTypeEnum.FORM;
    }

    @Override
    public WeOpenConfig getWeOpenConfig(SysShortUrlMapping mapping) {
        String corpId = mapping.getFormAppendInfo().getCorpId();
        String appId = mapping.getFormAppendInfo().getAppId();
        return wechatOpenService.getConfig(corpId, appId);
    }
}
