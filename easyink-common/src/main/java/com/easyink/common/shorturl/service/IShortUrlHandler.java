package com.easyink.common.shorturl.service;


import com.easyink.common.shorturl.model.SysShortUrlMapping;

/**
 * 类名: 短链业务处理接口 (包含相应的业务处理)
 *
 * @author : silver_chariot
 * @date : 2022/8/15 17:55
 **/
public interface IShortUrlHandler<T> {
    /**
     * 根据长链接创建短链接
     *
     * @param corpId     企业id
     * @param url        长链接
     * @param createBy   创建人
     * @param appendInfo 附加信息,非必传, 需继承{@link com.easyink.common.shorturl.model.BaseShortUrlAppendInfo}, 每个短链处理类型的附加信息不同,比如：
     *                   当前只有type={@link com.easyink.common.shorturl.enums.ShortUrlTypeEnum#RADAR} 的时候,
     *                   需要传 {@link com.easyink.common.shorturl.model.RadarShortUrlAppendInfo} 转成jsonString的字符串格式
     * @return 生成的短链接
     */
    String createShortUrl(String corpId, String url, String createBy, T appendInfo);

    /**
     * 对点击短链事件进行业务处理并返回需要重定向的url
     * (调用前可通过 {@link ShortUrlService#getUrlByMapping(String)} 获取映射实体
     *
     * @param mapping 短链->长链的映射关系
     * @return 需要302重定向的url
     */
    String handleAndGetRedirectUrl(SysShortUrlMapping mapping);

}
