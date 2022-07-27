package com.easywecom.wecom.service.wechatopen.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.http.ForestRequest;
import com.easywecom.common.config.WechatOpenConfig;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.shorturl.SysShortUrlMapping;
import com.easywecom.common.shorturl.service.ShortUrlAdaptor;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WechatOpenClient;
import com.easywecom.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easywecom.wecom.domain.resp.GetAccessTokenResp;
import com.easywecom.wecom.domain.resp.GetOfficialAuthInfoResp;
import com.easywecom.wecom.domain.resp.SnsUserInfoResp;
import com.easywecom.wecom.domain.vo.AppIdVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.wechatopen.WeOpenConfigMapper;
import com.easywecom.wecom.service.wechatopen.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 类名: 微信公众平台业务处理接口实现类
 *
 * @author : silver_chariot
 * @date : 2022/7/20 10:49
 **/
@Service
@Slf4j
public class WechatOpenServiceImpl extends ServiceImpl<WeOpenConfigMapper, WeOpenConfig> implements WechatOpenService {
    /**
     * grant_type默认传参
     */
    private static final String AUTH_CODE_GRANT_TYPE = "authorization_code";
    private static final String CLIENT_CREDENTIAL_GRANT_TYPE = "client_credential";
    private static final String ACCESS_TOKEN = "access_token";
    private final static String CN_LANG = "zh_CN";
    private final WechatOpenConfig wechatOpenConfig;
    private final WechatOpenClient wechatOpenClient;
    private final RedisCache redisCache;
    private final ShortUrlAdaptor shortUrlAdaptor;

    @Lazy
    public WechatOpenServiceImpl(WechatOpenConfig wechatOpenConfig, WechatOpenClient wechatOpenClient, RedisCache redisCache, ShortUrlAdaptor shortUrlAdaptor) {
        this.wechatOpenConfig = wechatOpenConfig;
        this.wechatOpenClient = wechatOpenClient;
        this.redisCache = redisCache;
        this.shortUrlAdaptor = shortUrlAdaptor;
    }

    @Override
    public AppIdVO getAppId(String shortCode) {
        if (StringUtils.isBlank(shortCode)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 获取长链
        SysShortUrlMapping mapping = shortUrlAdaptor.getLongUrlMapping(shortCode);
        if (mapping == null || mapping.getAppendInfo() == null || StringUtils.isBlank(mapping.getAppend().getCorpId())) {
            log.error("[获取appid]根据短链code获取corpId失败,{}", shortCode);
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        String corpId = mapping.getAppend().getCorpId();
        // 获取
        WeOpenConfig config = getConfig(corpId);
        if (config == null || StringUtils.isBlank(config.getOfficialAccountAppId())) {
            throw new CustomException(ResultTip.TIP_MISS_APPID);
        }
        return AppIdVO.builder()
                .corpId(corpId)
                .appId(config.getOfficialAccountAppId())
                .build();
    }

    @Override
    public String getOpenId(String code, String corpId) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException(ResultTip.TIP_MISSING_USER_CODE);
        }
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        WeOpenConfig config = getConfig(corpId);
        if (config == null || StringUtils.isAnyBlank(config.getOfficialAccountAppSecret(), config.getOfficialAccountAppId())) {
            throw new CustomException(ResultTip.TIP_NO_APP_SECRET);
        }
        GetOfficialAuthInfoResp resp = wechatOpenClient.getAuthInfo(config.getOfficialAccountAppId(), config.getOfficialAccountAppSecret(), code, AUTH_CODE_GRANT_TYPE, corpId);
        if (resp.isError() || StringUtils.isBlank(resp.getOpenId())) {
            throw new CustomException(ResultTip.TIP_WECHAT_OPEN_GET_AUTH_ERROR);
        }
        // 保存对应openid的sns_access_token
        redisCache.setCacheObject(snsRedisKey(resp.getOpenId()), resp.getAccess_token());
        return resp.getOpenId();
    }

    public String snsRedisKey(String openId) {
        return "wechatOpen:accessToken:" + openId;
    }

    @Override
    public void setAccessToken(ForestRequest request) {
        if (request == null) {
            return;
        }
        if (wechatOpenConfig == null || wechatOpenConfig.getOfficialAccount() == null
                || StringUtils.isAnyBlank(wechatOpenConfig.getOfficialAccount().getAppId(), wechatOpenConfig.getOfficialAccount().getAppSecret())) {
            log.info("[微信公众平台请求]没有设置appid和appsecret,无法生成access_token");
            return;
        }
        String appId = wechatOpenConfig.getOfficialAccount().getAppId();
        // 先从缓存中获取
        String corpId = request.getHeader("corpId").getValue();
        String redisKey = getAccessTokenKey(appId, corpId);
        String accessToken = redisCache.getCacheObject(redisKey);
        if (StringUtils.isNotBlank(accessToken)) {
            request.addQuery(ACCESS_TOKEN, accessToken);
            return;
        }
        String secret = wechatOpenConfig.getOfficialAccount().getAppSecret();
        // 没有则请求开放平台重新获取
        GetAccessTokenResp resp = wechatOpenClient.getAccessToken(CLIENT_CREDENTIAL_GRANT_TYPE, appId, secret);
        if (resp.isError() || StringUtils.isBlank(resp.getAccess_token())) {
            log.error("[微信公众平台请求] 获取access_token失败,appId:{},resp:{}", appId, resp);
            return;
        }
        // 响应成功则设置缓存 并设置请求的access_token
        redisCache.setCacheObject(redisKey, resp.getAccess_token(), resp.getExpires_in(), TimeUnit.SECONDS);
        request.addQuery(ACCESS_TOKEN, resp.getAccess_token());
    }

    @Override
    public String getUnionIdByOpenId(String openId) {
        if (StringUtils.isBlank(openId)) {
            throw new CustomException(ResultTip.TIP_OPEN_ID_CANNOT_BE_NULL);
        }
        // 根据openid获取其对应的sns_access_token
        String snsAccessToken = redisCache.getCacheObject(snsRedisKey(openId));
        SnsUserInfoResp snsUserInfoResp = wechatOpenClient.snsUserInfo(snsAccessToken, openId, CN_LANG);
        if (snsUserInfoResp.isError() || StringUtils.isBlank(snsUserInfoResp.getUnionid())) {
            log.info("[获取unionId]获取unionid失败,openid:{}.snsToken:{}", openId, snsAccessToken);
            throw new CustomException(ResultTip.TIP_ERROR_GET_UNION_ID);
        }
        return snsUserInfoResp.getUnionid();
    }

    @Override
    public WeOpenConfig getConfig(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        WeOpenConfig config = this.baseMapper.selectOne(new LambdaQueryWrapper<WeOpenConfig>()
                .eq(WeOpenConfig::getCorpId, corpId)
                .last(GenConstants.LIMIT_1));
        return config;
    }

    @Override
    public void updateConfig(WeOpenConfig config) {
        if (config == null || StringUtils.isBlank(config.getOfficialAccountAppId())) {
            throw new CustomException(ResultTip.TIP_NO_APP_ID_CONFIG);
        }
        LoginUser loginUser = LoginTokenService.getLoginUser();
        // 获取原有配置
        config.setCorpId(loginUser.getCorpId());
        this.baseMapper.insertOrUpdate(config);
    }

    /**
     * 获取微信小程序的accessToken
     *
     * @param appId  小程序appId
     * @param corpId 企业id
     * @return
     */
    public String getAccessTokenKey(String appId, String corpId) {
        return "wechatOpen:" + corpId + ":accessToken:" + appId;
    }
}
