package com.easyink.common.shorturl.service;

import com.easyink.common.shorturl.model.BaseShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;

/**
 * 类名: 短链处理接口 (不包含业务)
 *
 * @author : silver_chariot
 * @date : 2022/7/18 16:42
 **/
public interface ShortUrlService {
    /**
     * 创建短链
     *
     * @param longUrl       长链接
     * @param type          短链类型 {@link com.easyink.common.shorturl.enums.ShortUrlTypeEnum}
     * @param createBy      创建人,如果是员工则是userId,管理员为admin
     * @param appendInfoStr 附加信息(用于埋点,非必传)转json字符串 需继承{@link BaseShortUrlAppendInfo }
     * @return 短链接
     */
    String createShortCode(String longUrl, Integer type, String createBy, String appendInfoStr);

    /**
     * 根据短链后面的字符串 获取其原有的长链接
     *
     * @param shortCode 短链接后面的字符串
     * @return 对应的原有的长链接
     */
    SysShortUrlMapping getUrlByMapping(String shortCode);
}
