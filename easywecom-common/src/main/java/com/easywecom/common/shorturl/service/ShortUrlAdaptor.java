package com.easywecom.common.shorturl.service;

import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.shorturl.ShortUrlAppendInfo;
import com.easywecom.common.shorturl.SysShortUrlMapping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 类名: 短链服务适配器 (对应业务继承后 可依据自身业务实现创建短链和获取长链)
 *
 * @author : silver_chariot
 * @date : 2022/7/19 17:50
 **/
@Component("shortUrlAdaptor")
public class ShortUrlAdaptor {

    @Resource(name = "shortUrlService")
    private ShortUrlService shortUrlService;

    /**
     * 创建短链
     *
     * @param longUrl  长链接
     * @param createBy 创建人,如果是员工则是userId,管理员为admin
     * @return 短链接
     */
    public String createShortCode(String longUrl, String createBy) {
        return shortUrlService.createShortCode(longUrl, createBy, null);
    }

    /**
     * 创建短链
     *
     * @param longUrl    长链接
     * @param createBy   创建人,如果是员工则是userId,管理员为admin
     * @param appendInfo 附件信息{@link ShortUrlAppendInfo}
     * @return 短链接
     */
    public String createShortCode(String longUrl, String createBy, ShortUrlAppendInfo appendInfo) {
        return shortUrlService.createShortCode(longUrl, createBy, appendInfo);
    }

    /**
     * 创建短链
     *
     * @param longUrl 长链接
     * @return 短链接
     */
    public String createShortCode(String longUrl) {
        return shortUrlService.createShortCode(longUrl, "admin", null);

    }

    /**
     * 根据短链后面的字符串 获取其原有的长链接映射详情
     *
     * @param shortCode 短链接后面的字符串
     * @return 对应的原有的长链接映射 {@link SysShortUrlMapping }
     */
    public SysShortUrlMapping getLongUrlMapping(String shortCode) {
        return shortUrlService.getUrlByMapping(shortCode);
    }

    /**
     * 根据短链后面的字符串 获取其原有的长链接
     *
     * @param shortCode 短链接后面的字符串
     * @return 对应的原有的长链接
     */
    public String getLongUrl(String shortCode) {
        SysShortUrlMapping mapping = getLongUrlMapping(shortCode);
        if (mapping != null && StringUtils.isNotBlank(mapping.getLongUrl())) {
            return mapping.getLongUrl();
        }
        return null;
    }

    /**
     * 生成完整的短链
     *
     * @param domain 域名
     * @param code   短链后缀的字符串
     * @return 完整的短链
     */
    public String genShortUrl(String domain, String code) {
        return domain + WeConstans.SLASH + code;
    }


}
