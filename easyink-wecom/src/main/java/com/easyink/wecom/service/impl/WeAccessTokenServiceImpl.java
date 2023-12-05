package com.easyink.wecom.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.wecom.client.We3rdAppClient;
import com.easyink.wecom.client.WeAccessTokenClient;
import com.easyink.wecom.domain.WeAuthCorpInfo;
import com.easyink.wecom.domain.dto.WeAccessTokenDTO;
import com.easyink.wecom.domain.dto.app.WeSuiteTokenReq;
import com.easyink.wecom.domain.dto.app.WeSuiteTokenResp;
import com.easyink.wecom.service.WeAccessTokenService;
import com.easyink.wecom.service.WeAuthCorpInfoService;
import com.easyink.wecom.service.WeCorpAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @description: 微信token相关接口
 * @create: 2020-08-26 14:43
 **/
@Service
@Slf4j
public class WeAccessTokenServiceImpl implements WeAccessTokenService {

    private String noUseCorpidSecret = "请联系管理员进行企业微信配置";
    private final WeAccessTokenClient accessTokenClient;
    private final WeCorpAccountService iWxCorpAccountService;
    private final RedisCache redisCache;
    private final We3rdAppClient we3rdAppClient;
    private final WeAuthCorpInfoService weAuthCorpInfoService;
    private final RuoYiConfig ruoYiConfig;

    @Autowired
    @Lazy
    public WeAccessTokenServiceImpl(WeAccessTokenClient accessTokenClient, WeCorpAccountService iWxCorpAccountService, RedisCache redisCache, We3rdAppClient we3rdAppClient, WeAuthCorpInfoService weAuthCorpInfoService, RuoYiConfig ruoYiConfig) {
        this.accessTokenClient = accessTokenClient;
        this.iWxCorpAccountService = iWxCorpAccountService;
        this.redisCache = redisCache;
        this.we3rdAppClient = we3rdAppClient;
        this.weAuthCorpInfoService = weAuthCorpInfoService;
        this.ruoYiConfig = ruoYiConfig;
    }


    /**
     * 获取使用企业Secert获取的凭证
     *
     * @param corpId 企业ID
     * @return 凭证
     */
    @Override
    public String findCommonAccessToken(String corpId) {
        return findAccessToken(WeConstans.WE_COMMON_ACCESS_TOKEN, corpId);
    }


    /**
     * 获取使用通讯录Secert获取的凭证
     *
     * @param corpId 企业ID
     * @return 凭证
     */
    @Override
    public String findContactAccessToken(String corpId) {
        return findAccessToken(WeConstans.WE_CONTACT_ACCESS_TOKEN, corpId);
    }

    /**
     * 获取使用服务商Secert获取的凭证
     *
     * @return 凭证
     */
    @Override
    public String findProviderAccessToken() {
        String weAccessToken = redisCache.getCacheObject(WeConstans.WE_PROVIDER_ACCESS_TOKEN);
        if (StringUtils.isNotBlank(weAccessToken)) {
            return weAccessToken;
        }
        //为空,请求微信服务器同时缓存到redis中
        String token;
        Long expiresIn;
        WeAccessTokenDTO weAccessTokenDTO = accessTokenClient.getProviderToken(ruoYiConfig.getProvider().getCorpId(), ruoYiConfig.getProvider().getSecret());
        token = weAccessTokenDTO.getProvider_access_token();
        expiresIn = weAccessTokenDTO.getExpires_in();
        //缓存suitetoken
        if (StringUtils.isNotEmpty(token)) {
            redisCache.setCacheObject(WeConstans.WE_PROVIDER_ACCESS_TOKEN, token, expiresIn.intValue(), TimeUnit.SECONDS);
            weAccessToken = token;
        }
        return weAccessToken;
    }


    /**
     * 获取使用会话存档Secert获取的凭证
     *
     * @return 凭证
     */
    @Override
    public String findChatAccessToken(String corpId) {
        return findAccessToken(WeConstans.WE_CHAT_ACCESS_TOKEN, corpId);
    }

    /**
     * 获取服务商应用凭证
     *
     * @return AccessToken
     */
    @Override
    public String findSuiteAccessToken(String suiteId) {
        String cacheTokenKey = WeConstans.WE_SUITE_ACCESS_TOKEN + suiteId;
        String weAccessToken = redisCache.getCacheObject(cacheTokenKey);
        if (StringUtils.isNotBlank(weAccessToken)) {
            return weAccessToken;
        }
        //为空,请求微信服务器同时缓存到redis中
        String secret = ruoYiConfig.getProvider().getSecretById(suiteId);
        if (StringUtils.isAnyBlank(suiteId, secret)) {
            throw new CustomException(ResultTip.TIP_MISS_PROVIDER_CONFIG);
        }
        String cacheTicketKey = WeConstans.WE_SUITE_TICKET + suiteId;
        String suiteTicket = redisCache.getCacheObject(cacheTicketKey);
        if (StringUtils.isBlank(suiteTicket)) {
            throw new CustomException(ResultTip.TIP_MISS_SUITE_TICKET);
        }

        String token;
        Long expiresIn;
        WeSuiteTokenReq weSuiteTokenReq = new WeSuiteTokenReq(suiteId, secret, suiteTicket);
        WeSuiteTokenResp weSuiteTokenResp = accessTokenClient.getSuiteToken(weSuiteTokenReq);
        token = weSuiteTokenResp.getSuiteAccessToken();
        expiresIn = weSuiteTokenResp.getExpiresIn();
        //缓存suitetoken
        if (StringUtils.isNotEmpty(token)) {
            redisCache.setCacheObject(cacheTokenKey, token, expiresIn.intValue(), TimeUnit.SECONDS);
            weAccessToken = token;
        }
        return weAccessToken;
    }

    /**
     * 获取三方应用授权企业凭证
     *
     * @param corpid 企业id
     * @return AccessToken
     */
    @Override
    public String find3rdAppCorpAuthAccessToken(String corpid) {
        if (StringUtils.isBlank(corpid)) {
            throw new WeComException("corpid参数缺失");
        }

        String redisKey = WeConstans.WE_AUTH_CORP_ACCESS_TOKEN + corpid;
        String weAccessToken = redisCache.getCacheObject(redisKey);
        if (StringUtils.isNotBlank(weAccessToken)) {
            return weAccessToken;
        }

        WeAuthCorpInfo weAuthCorpInfo = weAuthCorpInfoService.getOne(corpid);
        if (ObjectUtil.isEmpty(weAuthCorpInfo)) {
            throw new WeComException("企业未进行授权");
        }

        WeAccessTokenDTO weAccessTokenDTO = we3rdAppClient.getCorpToken(corpid, weAuthCorpInfo.getPermanentCode(), weAuthCorpInfo.getSuiteId());
        String token = weAccessTokenDTO.getAccess_token();
        Long expiresIn = weAccessTokenDTO.getExpires_in();
        //缓存企业token
        if (StringUtils.isNotEmpty(token)) {
            redisCache.setCacheObject(redisKey, token, expiresIn.intValue(), TimeUnit.SECONDS);
            weAccessToken = token;
        }
        return weAccessToken;
    }


    /**
     * 获取内部应用Secert的凭证
     *
     * @param agentId 应用ID
     * @param corpId  企业ID
     * @return AccessToken
     */
    @Override
    public String findInternalAppAccessToken(String agentId, String corpId) {
        String redisKey = WeConstans.WE_THIRD_APP_TOKEN + ":" + corpId + ":" + agentId;
        String token = redisCache.getCacheObject(redisKey);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        WeCorpAccount wxCorpAccount = iWxCorpAccountService.findValidWeCorpAccount(corpId);
        if (wxCorpAccount == null) {
            throw new WeComException(noUseCorpidSecret);
        }

        WeAccessTokenDTO weAccessTokenDTO = accessTokenClient.getToken(wxCorpAccount.getCorpId(), wxCorpAccount.getAgentSecret());

        if (StringUtils.isNotEmpty(weAccessTokenDTO.getAccess_token())) {
            redisCache.setCacheObject(redisKey, weAccessTokenDTO.getAccess_token(), weAccessTokenDTO.getExpires_in().intValue(), TimeUnit.SECONDS);
        }
        return weAccessTokenDTO.getAccess_token();

    }


    private String findAccessToken(String accessTokenKey, String corpId) {
        String redisKey = accessTokenKey + ":" + corpId;
        String weAccessToken = redisCache.getCacheObject(redisKey);
        log.info("[获取Access-Token] 当前获取的key：{}，corpId：{}", redisKey, corpId);
        //为空,请求微信服务器同时缓存到redis中
        if (StringUtils.isNotBlank(weAccessToken)) {
            return weAccessToken;
        }
        WeCorpAccount wxCorpAccount = iWxCorpAccountService.findValidWeCorpAccount(corpId);
        if (null == wxCorpAccount) {
            //返回错误异常，让用户绑定企业id相关信息
            throw new WeComException(noUseCorpidSecret);
        }
        String token = "";
        Long expiresIn = 0L;
        WeAccessTokenDTO weAccessTokenDTO;
        //获取不同类型的token
        switch (accessTokenKey) {
            case WeConstans.WE_CHAT_ACCESS_TOKEN:
                weAccessTokenDTO = accessTokenClient.getToken(wxCorpAccount.getCorpId(), wxCorpAccount.getChatSecret());
                log.info("[获取会话存档相关token] 使用的secret：{}，corpId：{}", wxCorpAccount.getChatSecret(), corpId);
                break;
            case WeConstans.WE_AGENT_ACCESS_TOKEN:
                weAccessTokenDTO = accessTokenClient.getToken(wxCorpAccount.getCorpId(), wxCorpAccount.getAgentSecret());
                log.info("[获取应用相关token] 使用的secret：{}，corpId：{}", wxCorpAccount.getAgentSecret(), corpId);
                break;
            case WeConstans.WE_CONTACT_ACCESS_TOKEN:
                weAccessTokenDTO = accessTokenClient.getToken(wxCorpAccount.getCorpId(), wxCorpAccount.getContactSecret());
                log.info("[获取获取内部联系人相关token] 使用的secret：{}，corpId：{}", wxCorpAccount.getContactSecret(), corpId);
                break;
            default:
                weAccessTokenDTO = accessTokenClient.getToken(wxCorpAccount.getCorpId(), wxCorpAccount.getCorpSecret());
                log.info("[获取Common相关token] 使用的secret：{}，corpId：{}", wxCorpAccount.getCorpSecret(), corpId);
        }
        if (ObjectUtils.isNotEmpty(weAccessTokenDTO) && StringUtils.isNotBlank(weAccessTokenDTO.getAccess_token())) {
            token = weAccessTokenDTO.getAccess_token();
        }
        if (ObjectUtils.isNotEmpty(weAccessTokenDTO) && ObjectUtils.isNotEmpty(weAccessTokenDTO.getExpires_in())) {
            expiresIn = weAccessTokenDTO.getExpires_in();
        }
        //缓存token
        if (StringUtils.isNotBlank(token)) {
            redisCache.setCacheObject(redisKey, token, expiresIn.intValue(), TimeUnit.SECONDS);
            log.info("[获取Access-Token] 存入redis的key：{}，corpId：{}，token：{}", redisKey, corpId, token);
            weAccessToken = token;
        }
        return weAccessToken;
    }


    /**
     * 清空redis中的相关token
     */
    @Override
    public void removeToken(WeCorpAccount wxCorpAccount) {
        if (wxCorpAccount == null || StringUtils.isBlank(wxCorpAccount.getCorpId())) {
            //如果配置中没有corpId则直接不处理
            return;
        }
        redisCache.deleteObject(WeConstans.WE_COMMON_ACCESS_TOKEN + ":" + wxCorpAccount.getCorpId());
        redisCache.deleteObject(WeConstans.WE_CONTACT_ACCESS_TOKEN + ":" + wxCorpAccount.getCorpId());
        redisCache.deleteObject(WeConstans.WE_CORP_ACCOUNT + ":" + wxCorpAccount.getCorpId());
        //内部应用缓存的企业配置是没有corpid的
        if (ruoYiConfig.isInternalServer()) {
            redisCache.deleteObject(WeConstans.WE_CORP_ACCOUNT);
        }
        String redisKey = WeConstans.WE_CORP_ACCOUNT + ":" + wxCorpAccount.getCorpId();
        redisCache.deleteObject(redisKey);

    }


}
