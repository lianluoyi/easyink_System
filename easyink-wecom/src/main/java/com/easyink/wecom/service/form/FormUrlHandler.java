package com.easyink.wecom.service.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.ShortCodeType;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.FormShortUrlAppendInfo;
import com.easyink.common.shorturl.RadarShortUrlAppendInfo;
import com.easyink.common.shorturl.service.ShortUrlAdaptor;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.entity.form.WeFormShortCodeRel;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 表单链接处理类
 *
 * @author wx
 * 2023/1/15 11:30
 **/
@Slf4j
@Component(value = "formUrlHandler")
@RequiredArgsConstructor
public class FormUrlHandler extends ShortUrlAdaptor {

    private final WechatOpenService wechatOpenService;

    private final WeFormShortCodeRelService weFormShortCodeRelService;

    /**
     * 构建短链附加信息
     *
     * @param formId      表单id
     * @param userId      使用表单用户id
     * @param channelType 渠道类型 {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @param appId       公众号appId
     * @param corpId      企业id
     * @return {@link FormShortUrlAppendInfo }
     */
    public FormShortUrlAppendInfo buildAppendInfo(Integer formId, String userId, Integer channelType, String appId, String corpId) {
        return FormShortUrlAppendInfo.builder().type(ShortCodeType.FORM.getCode()).formId(formId).userId(userId).channelType(channelType).appId(appId).corpId(corpId).build();
    }

    /**
     * 创建表单短链
     *
     * @param appId      公众号appId
     * @param corpId     企业id
     * @param url        长链接url
     * @param createBy   创建人
     * @param appendInfo 附件信息{@link RadarShortUrlAppendInfo }
     * @return 表单短链
     */
    public String createFormUrl(String appId, String corpId, String url, String createBy, FormShortUrlAppendInfo appendInfo) {
        if (StringUtils.isAnyBlank(appId, url, corpId)) {
            log.info("[创建表单短链] 缺失长链接或者corpId,by:{},append:{},corpId:{}", createBy, appendInfo, corpId);
            throw new CustomException(ResultTip.TIP_MISSING_LONG_URL);
        }

        // 生成短链code
        String code = createShortCode(url, createBy, appendInfo);
        if (StringUtils.isBlank(code)) {
            throw new CustomException(ResultTip.TIP_ERROR_CREATE_SHORT_URL);
        }
        String officeDomain = wechatOpenService.getDomain(corpId);
        if(StringUtils.isBlank(officeDomain)) {
            throw new CustomException(ResultTip.TIP_WECHAT_OPEN_OFFICIAL_NO_DOMAIN);
        }
        // 保存到表单短链关联表
        weFormShortCodeRelService.save(new WeFormShortCodeRel(appendInfo.getFormId(), code, appendInfo.getUserId(), DateUtils.getNowDate()));
        return genShortUrl(officeDomain, code);
    }

}


