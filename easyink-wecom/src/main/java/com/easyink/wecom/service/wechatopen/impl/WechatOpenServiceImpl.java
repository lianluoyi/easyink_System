package com.easyink.wecom.service.wechatopen.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.http.ForestRequest;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.WechatOpenConfig;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.form.FormConstants;
import com.easyink.common.constant.wechatopen.WechatOpenConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.redis.WechatOpenConfigRedisCache;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.common.shorturl.service.ShortUrlService;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WechatOpen3rdClient;
import com.easyink.wecom.client.WechatOpenClient;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.resp.GetAccessTokenResp;
import com.easyink.wecom.domain.resp.GetOfficialAuthInfoResp;
import com.easyink.wecom.domain.resp.SnsUserInfoResp;
import com.easyink.wecom.domain.resp.WechatOpen3rdResp;
import com.easyink.wecom.domain.vo.AppIdVO;
import com.easyink.wecom.domain.vo.WeOpenConfigVO;
import com.easyink.wecom.domain.vo.WeServerTypeVO;
import com.easyink.wecom.handler.shorturl.ShortUrlHandlerFactory;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.form.WeFormMapper;
import com.easyink.wecom.mapper.wechatopen.WeOpenConfigMapper;
import com.easyink.wecom.service.We3rdAppService;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.form.WeFormAdvanceSettingService;
import com.easyink.wecom.service.form.WeFormService;
import com.easyink.wecom.service.radar.WeRadarOfficialAccountConfigService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private static final String CN_LANG = "zh_CN";
    private final WechatOpenConfig wechatOpenConfig;
    private final WechatOpenClient wechatOpenClient;
    private final RedisCache redisCache;
    private final ShortUrlService shortUrlService;
    @Resource(name = "wechatOpenConfigRedisCache")
    private WechatOpenConfigRedisCache wechatOpenConfigRedisCache;
    private final WechatOpen3rdClient wechatOpen3rdClient;
    private final RuoYiConfig ruoYiConfig;
    private final WeFormAdvanceSettingService weFormAdvanceSettingService;

    private final WeFormMapper weFormMapper;
    private final ShortUrlHandlerFactory shortUrlHandlerFactory;
    private final WeCustomerService weCustomerService;

    private final WeOpenConfigMapper weOpenConfigMapper;
    private final WeCorpAccountService weCorpAccountService;

    @Resource(name = "formTaskExecutor")
    private ThreadPoolTaskExecutor formTaskExecutor;
    @Lazy
    public WechatOpenServiceImpl(WechatOpenConfig wechatOpenConfig, WechatOpenClient wechatOpenClient, RedisCache redisCache, WechatOpen3rdClient wechatOpen3rdClient, WeRadarOfficialAccountConfigService weRadarOfficialAccountConfigService, ShortUrlService shortUrlService, RuoYiConfig ruoYiConfig, WeFormAdvanceSettingService weFormAdvanceSettingService, WeFormMapper weFormMapper, WeFormService weFormService, ShortUrlHandlerFactory shortUrlHandlerFactory, WeCustomerService weCustomerService, WeOpenConfigMapper weOpenConfigMapper, WeCorpAccountService weCorpAccountService) {
        this.wechatOpenConfig = wechatOpenConfig;
        this.wechatOpenClient = wechatOpenClient;
        this.redisCache = redisCache;
        this.shortUrlService = shortUrlService;
        this.wechatOpen3rdClient = wechatOpen3rdClient;
        this.ruoYiConfig = ruoYiConfig;
        this.weFormAdvanceSettingService = weFormAdvanceSettingService;
        this.weFormMapper = weFormMapper;
        this.shortUrlHandlerFactory = shortUrlHandlerFactory;
        this.weCustomerService = weCustomerService;
        this.weOpenConfigMapper = weOpenConfigMapper;
        this.weCorpAccountService = weCorpAccountService;
    }

    @Override
    public AppIdVO getAppIdByShortCode(String shortCode) {
        return buildAppIdVO(getWeOpenConfigByShortCode(shortCode));
    }

    @Override
    public AppIdVO getAppIdByFormId(Long formId) {
        // 智能表单获取公众号配置
        return buildAppIdVO(getFormWeOpenConfig(formId));
    }

    /**
     * 组装返回appIdVO
     *
     * @param config    {@link WeOpenConfig}
     * @return  AppIdVO
     */
    private AppIdVO buildAppIdVO(WeOpenConfig config) {
        if(config == null || StringUtils.isBlank(config.getOfficialAccountAppId())) {
            throw  new CustomException(ResultTip.TIP_MISS_APPID);
        }
        AppIdVO appIdVO = AppIdVO.builder()
                .corpId(config.getCorpId())
                .appId(config.getOfficialAccountAppId())
                .build();
        if(ruoYiConfig.isThirdServer()) {
            appIdVO.setComponentAppId(wechatOpenConfig.getPlatform3rdAccount().getAppId());
        }
        log.info("[获取appId] appIdVO:{}",appIdVO);
        return appIdVO;
    }

    /**
     * 获取智能表单公众号配置
     *
     * @param formId    表单id
     * @return
     */
    private WeOpenConfig getFormWeOpenConfig(Long formId) {
        if (null == formId) {
            return null;
        }
        CompletableFuture<WeFormAdvanceSetting> weFormAdvanceSettingCf = CompletableFuture.supplyAsync(() -> weFormAdvanceSettingService.getOne(new LambdaQueryWrapper<WeFormAdvanceSetting>()
                .eq(WeFormAdvanceSetting::getFormId, formId)), formTaskExecutor);
        CompletableFuture<WeForm> weFormCf = CompletableFuture.supplyAsync(() -> weFormMapper.selectByIdIgnoreDelete(formId), formTaskExecutor);
        try {
            int timeout = 3;
            CompletableFuture.allOf(weFormCf, weFormCf).exceptionally(e -> {
                log.error("[获取表单公众号设置] 查询异常, formId:{}, e: {}", formId, ExceptionUtils.getStackTrace(e));
                throw new CustomException(ExceptionUtils.getMessage(e));
            }).get(timeout, TimeUnit.SECONDS);

            WeFormAdvanceSetting weFormAdvanceSetting = weFormAdvanceSettingCf.get(timeout, TimeUnit.SECONDS);
            WeForm weForm = weFormCf.get(timeout, TimeUnit.SECONDS);
            return getConfig(weForm.getCorpId(), weFormAdvanceSetting.getWeChatPublicPlatform());
        } catch (InterruptedException e) {
            log.error("[获取表单公众号设置] 查询表单公众号配置异常, formId:{}, e:{}", formId, ExceptionUtils.getMessage(e));
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException | TimeoutException e) {
            log.error("[获取表单公众号设置] 查询表单公众号配置异常, formId:{}, e:{}", formId, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 获取雷达的公共号配置
     *
     * @param shortCode 短链
     * @return
     */
    private WeOpenConfig getWeOpenConfigByShortCode(String shortCode) {
        log.info("[获取appId] shortCode:{}",shortCode);
        if (StringUtils.isBlank(shortCode)) {
            return null;
        }
        // 获取短链映射
        SysShortUrlMapping mapping = shortUrlService.getUrlByMapping(shortCode);
        if (mapping == null || mapping.getAppendInfo() == null || StringUtils.isBlank(mapping.getBaseAppendInfo().getCorpId())) {
            log.error("[通过短链获取appid] 根据短链code获取corpId失败,{}", shortCode);
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return shortUrlHandlerFactory.getByType(mapping.getType()).getWeOpenConfig(mapping);
    }


    @Override
    public String getOpenId(String code, String corpId, String appId) {
        log.info("[获取openId] code:{}, corpId:{}, appId:{}", code, corpId, appId);
        if (StringUtils.isBlank(code)) {
            throw new CustomException(ResultTip.TIP_MISSING_USER_CODE);
        }
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        WeOpenConfig config = getConfig(corpId, appId);

        GetOfficialAuthInfoResp resp = null;
        if (ruoYiConfig.isInternalServer()) {
            if (config == null || StringUtils.isAnyBlank(config.getOfficialAccountAppSecret(), config.getOfficialAccountAppId())) {
                throw new CustomException(ResultTip.TIP_NO_APP_SECRET);
            }
            resp = wechatOpenClient.getAuthInfo(config.getOfficialAccountAppId(), config.getOfficialAccountAppSecret(), code, AUTH_CODE_GRANT_TYPE, corpId);
        } else {
            if (config == null || StringUtils.isBlank(config.getOfficialAccountAppId())) {
                throw new CustomException(ResultTip.TIP_NO_APP_ID_CONFIG);
            }
            resp = wechatOpenClient.getAccessTokenByCode(config.getOfficialAccountAppId(), code, AUTH_CODE_GRANT_TYPE,
                    wechatOpenConfig.getPlatform3rdAccount().getAppId(), getPlatform3rdAccessToken(wechatOpenConfig.getPlatform3rdAccount().getAppId()));
        }
        if (resp == null || resp.isError() || StringUtils.isBlank(resp.getOpenId())) {
            throw new CustomException(ResultTip.TIP_WECHAT_OPEN_GET_AUTH_ERROR);
        }
        if (resp.getIs_snapshotuser() != null && WeConstans.WECHAT_OPEN_SNAP_SHOT_USER.equals(resp.getIs_snapshotuser())) {
            throw new CustomException(ResultTip.TIP_WECHAT_OPEN_IS_SNAP_SHOT_USER);
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
        if (wechatOpenConfig == null || wechatOpenConfig.getMiniApp() == null
                || StringUtils.isAnyBlank(wechatOpenConfig.getMiniApp().getAppId(), wechatOpenConfig.getMiniApp().getAppSecret())) {
            log.info("[微信公众平台请求]没有设置appid和appsecret,无法生成access_token");
            return;
        }
        String appId = wechatOpenConfig.getMiniApp().getAppId();
        // 先从缓存中获取
        String redisKey = getAccessTokenKey(appId);
        String accessToken = redisCache.getCacheObject(redisKey);
        if (StringUtils.isNotBlank(accessToken)) {
            request.addQuery(ACCESS_TOKEN, accessToken);
            return;
        }
        String secret = wechatOpenConfig.getMiniApp().getAppSecret();
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
    public String getPlatform3rdAccessToken(String appId) {
        if (wechatOpenConfig == null || wechatOpenConfig.getPlatform3rdAccount() == null
                || StringUtils.isAnyBlank(appId, wechatOpenConfig.getPlatform3rdAccount().getAppId(), wechatOpenConfig.getPlatform3rdAccount().getAppSecret())) {
            log.info("[微信开放平台-第三方平台请求]没有设置appid和appsecret,无法生成access_token");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String componentAccessToken = wechatOpenConfigRedisCache.getComponentAccessToken(appId);
        // 先从缓存中获取
        if (StringUtils.isNotBlank(componentAccessToken)) {
            return componentAccessToken;
        }
        // 如果缓存中没有accessToken，则请求开放平台重新获取
        // 获取检验票据
        String componentVerifyTicket = wechatOpenConfigRedisCache.getComponentVerifyTicket(appId);
        if (StringUtils.isBlank(componentVerifyTicket)) {
            log.error("[微信开放平台-三方平台获取票据失败] appid:{}", appId);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String secret = wechatOpenConfig.getPlatform3rdAccount().getAppSecret();

        WechatOpen3rdResp.ComponentAccessToken resp = wechatOpen3rdClient.getComponentAccessToken(secret, componentVerifyTicket);
        if (resp == null || StringUtils.isBlank(resp.getComponent_access_token()) || resp.getExpires_in() == null) {
            log.error("[微信开放平台-三方平台请求] 获取component_access_token失败,appId:{},resp:{}", appId, resp);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 响应成功则设置缓存 并设置请求的component_access_token
        wechatOpenConfigRedisCache.setComponentAccessToken(appId, resp.getComponent_access_token(), resp.getExpires_in());
        return resp.getComponent_access_token();
    }

    @Override
    public String getDomain(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        String domain;
        if (ruoYiConfig.isInternalServer()) {
            domain = wechatOpenConfig.getOfficialAccount().getDomain();
        } else {
            domain = wechatOpenConfig.getPlatform3rdAccount().getDomain();
        }
        if (StringUtils.isBlank(domain)) {
            throw new CustomException(ResultTip.NO_OFFICIAL_ACCOUNT_CONFIG);
        }
        return domain;
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
    public List<WeOpenConfigVO> getConfigs(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return this.baseMapper.getConfigList(corpId);
    }

    @Override
    public WeOpenConfig getConfig(String corpId, String appId) {
        if (StringUtils.isAnyBlank(corpId, appId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        return this.baseMapper.selectOne(new LambdaQueryWrapper<WeOpenConfig>()
                .eq(WeOpenConfig::getCorpId, corpId)
                .eq(WeOpenConfig::getOfficialAccountAppId, appId)
                .last(GenConstants.LIMIT_1));
    }

    @Override
    public void updateConfig(WeOpenConfig config, boolean isOnline) {
        if (config == null || StringUtils.isBlank(config.getOfficialAccountAppId())) {
            throw new CustomException(ResultTip.TIP_NO_APP_ID_CONFIG);
        }
        if (Boolean.TRUE.equals(isOnline)) {
            LoginUser loginUser = LoginTokenService.getLoginUser();
            // 获取原有配置
            config.setCorpId(loginUser.getCorpId());
            config.setOfficialAccountDomain(getDomain(config.getCorpId()));
            config.setUpdateBy(loginUser.getUserId());
            We3rdAppService we3rdAppService = SpringUtils.getBean(We3rdAppService.class);
            WeServerTypeVO serverType = we3rdAppService.getServerType();
            // 自建应用只保存一个
            if (ServerTypeEnum.INTERNAL.getType().equals(serverType.getServerType())) {
                weOpenConfigMapper.delete(new LambdaQueryWrapper<WeOpenConfig>().eq(WeOpenConfig::getCorpId, config.getCorpId()));
            }
        }
        SensitiveFieldProcessor.processForSave(config);
        this.baseMapper.insertOrUpdate(config);
        // 异步同步客户
        weCustomerService.syncWeCustomerV2(config.getCorpId());
    }

    @Override
    @Async
    public void addOrUpdate3rdConfig(String authorizationCode) {
        addOrUpdate3rdConfig(null, null, authorizationCode);
    }

    @Override
    public void addOrUpdate3rdConfig(String corpId, String userId, String authorizationCode) {
        if (StringUtils.isBlank(authorizationCode)) {
            log.info("[新增或更新微信三方平台公众号] 参数缺失 corpId:{}, authorizationCode:{}", corpId, authorizationCode);
            return;
        }
        // 获取授权号的authorizer_access_token 和 authorizer_refresh_token
        WechatOpen3rdResp authorizationInfo = wechatOpen3rdClient.getQueryAuth(authorizationCode);
        if (authorizationInfo == null || authorizationInfo.getAuthorization_info() == null
                || StringUtils.isAnyBlank(authorizationInfo.getAuthorization_info().getAuthorizer_appid(), authorizationInfo.getAuthorization_info().getAuthorizer_access_token(), authorizationInfo.getAuthorization_info().getAuthorizer_refresh_token())) {
            log.info("[微信开放平台-第三方平台获取调用凭据和授权信息] 失败, authorizationCode:{}, authorizationInfo:{}", authorizationCode, authorizationInfo);
            return;
        }
        String authorizerAppid = authorizationInfo.getAuthorization_info().getAuthorizer_appid();
        // 获取授权号基本信息
        WechatOpen3rdResp authorizerInfo = wechatOpen3rdClient.getAuthorizerInfo(authorizerAppid);
        if(authorizerInfo == null || authorizerInfo.getAuthorizer_info() == null) {
            log.info("[微信开放平台-第三方平台获取授权方的帐号基本信息] 失败 authorizerAppid:{}, authorizerInfo:{}", authorizerAppid, authorizerInfo);
            return;
        }
        WeOpenConfig config = authorizerInfo.getAuthorizer_info().getWechatOpenConfig();
        config.setCorpId(corpId);
        config.setCreateBy(userId);
        config.setOfficialAccountDomain(wechatOpenConfig.getPlatform3rdAccount().getDomain());
        config.setOfficialAccountAppId(authorizationInfo.getAuthorization_info().getAuthorizer_appid());
        config.setAuthorizerAccessToken(authorizationInfo.getAuthorization_info().getAuthorizer_access_token());
        config.setAuthorizerRefreshToken(authorizationInfo.getAuthorization_info().getAuthorizer_refresh_token());
        updateConfig(config, false);
        // 授权完成后, 同步客户
        weCustomerService.syncWeCustomerV2(corpId);
    }

    @Override
    @Async
    public void remove3rdConfig(String authorizerAppid) {
        if (StringUtils.isBlank(authorizerAppid)) {
            return;
        }
        this.baseMapper.delete(new LambdaQueryWrapper<WeOpenConfig>().eq(WeOpenConfig::getOfficialAccountAppId, authorizerAppid));
    }

    @Override
    public String getWechatOpen3rdAuthUrl(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        // 获取appid
        String appId = wechatOpenConfig.getPlatform3rdAccount().getAppId();
        // 获取授权码
        String preAuthCode = getPreAuthCode();
        // 获取userId 做为创建人
        String userId = LoginTokenService.getLoginUser().getUserId();
        // 获取回调域名
        String domain = wechatOpenConfig.getPlatform3rdAccount().getAuthInitPageDomain();
        return WechatOpenConstants.getWechatOpen3rdAuthUrl(appId, preAuthCode, domain, corpId, userId);
    }

    @Override
    public String handle3rdAuthOfficeAccount(String corpId, String userId, String authCode) {
        if (StringUtils.isBlank(authCode)) {
            log.info("[新增微信三方平台公众号] 参数缺失 corpId:{}, authorizationCode:{}", corpId, authCode);
            return StringUtils.EMPTY;
        }
        // 新增公众号配置
        addOrUpdate3rdConfig(corpId, userId, authCode);
        // 返回dashboard重定向地址
        WeCorpAccount validWeCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (validWeCorpAccount == null) {
            log.info("[新增微信三方平台公众号] 获取企业信息失败 corpId：{}", corpId);
            return StringUtils.EMPTY;
        }
        return FormConstants.getAlterAuthRedirectUrl(validWeCorpAccount.getWxQrLoginRedirectUri());
    }

    /**
     * 获取与授权码
     *
     * @return preAuthCode
     */
    private String getPreAuthCode() {
        String preAuthCode = wechatOpenConfigRedisCache.getPreAuthCode();
        if (StringUtils.isNotBlank(preAuthCode)) {
            return preAuthCode;
        }
        WechatOpen3rdResp.PreAuthCode preAuthCodeResp = wechatOpen3rdClient.getPreAuthCode();
        if (preAuthCodeResp == null || StringUtils.isBlank(preAuthCodeResp.getPre_auth_code())) {
            log.info("[微信三方平台] 获取授权码失败, preAuthCodeResp:{}", preAuthCodeResp);
            return null;
        }
        wechatOpenConfigRedisCache.setPreAuthCode(preAuthCodeResp.getPre_auth_code(), preAuthCodeResp.getExpires_in());
        return preAuthCodeResp.getPre_auth_code();
    }


    /**
     * 获取微信小程序的accessToken
     *
     * @param appId 小程序appId
     * @return
     */
    public String getAccessTokenKey(String appId) {
        return "wechatOpen" + ":accessToken:" + appId;
    }
}
