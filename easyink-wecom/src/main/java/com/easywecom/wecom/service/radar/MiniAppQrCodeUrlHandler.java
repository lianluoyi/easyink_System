package com.easywecom.wecom.service.radar;

import com.easywecom.common.config.WechatOpenConfig;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.shorturl.SysShortUrlMapping;
import com.easywecom.common.shorturl.service.ShortUrlAdaptor;
import com.easywecom.wecom.client.WechatOpenClient;
import com.easywecom.wecom.domain.req.GenerateUrlLinkReq;
import com.easywecom.wecom.domain.resp.GenerateUrlLinkResp;
import com.easywecom.wecom.login.util.LoginTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类名: 小程序活码短链处理类
 *
 * @author : silver_chariot
 * @date : 2022/7/19 17:44
 **/
@Component
@Slf4j
public class MiniAppQrCodeUrlHandler extends ShortUrlAdaptor {

    private final WechatOpenConfig wechatOpenConfig;
    private final WechatOpenClient wechatOpenClient;

    @Autowired
    private RedisCache redisCache;


    public MiniAppQrCodeUrlHandler(WechatOpenConfig wechatOpenConfig, WechatOpenClient wechatOpenClient) {
        this.wechatOpenConfig = wechatOpenConfig;
        this.wechatOpenClient = wechatOpenClient;
    }

    /**
     * 生成活码小程序短链
     *
     * @param qrCodeUrl 长链接
     * @return 活码小程序链接
     */
    public String createShortCode(String qrCodeUrl) {
        if (StringUtils.isBlank(qrCodeUrl)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }

        if (wechatOpenConfig.getMiniApp() == null || StringUtils.isAnyBlank(wechatOpenConfig.getMiniApp().getDomain(), wechatOpenConfig.getMiniApp().getCodePath())) {
            log.info("[活码小程序]生成短链,配置文件相关配置缺失{}", wechatOpenConfig.getMiniApp());
            // 考虑到不是每个企业都需要生成小程序活码,此处不作强制判断去生成短链
            return StringUtils.EMPTY;
        }
        // 先生成小程序的跳转link
        GenerateUrlLinkReq req = new GenerateUrlLinkReq().path(wechatOpenConfig.getMiniApp().getCodePath()).version(wechatOpenConfig.getMiniApp().getEnvVersion())
                //todo 生成 query字符串
//                .query(genQueryString(qrCodeUrl))
                .token(redisCache.getCacheObject(""))
                .buildReq();
        GenerateUrlLinkResp resp = wechatOpenClient.generateUrlLink(req);
        if (resp == null || StringUtils.isBlank(resp.getUrl_link())) {
            log.error("[活码小程序]生成小程序的跳转link失败,url:{},resp:{}", qrCodeUrl, resp);
            return StringUtils.EMPTY;
        }
        // 生成短链
        String shorCode = createShortCode(resp.getUrl_link(), LoginTokenService.getUsername());

        return  genShortUrl(wechatOpenConfig.getMiniApp().getDomain() , shorCode);
    }

    /**
     * 获取活码小程序的链接
     *
     * @param code 短链的code
     * @return 活码链接
     */
    public String getQrCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException(ResultTip.TIP_NEED_SHORT_CODE);
        }
        String longUrl = getLongUrl(code);
        if (StringUtils.isBlank(longUrl)) {
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_LONG_URL);
        }
        return longUrl;
    }
}
