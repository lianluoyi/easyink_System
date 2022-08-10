package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.wecom.client.We3rdAppClient;
import com.easyink.wecom.client.We3rdUserClient;
import com.easyink.wecom.domain.WeAuthCorpInfo;
import com.easyink.wecom.domain.WeAuthCorpInfoExtend;
import com.easyink.wecom.domain.dto.app.ToOpenCorpIdResp;
import com.easyink.wecom.domain.dto.app.WeAdminListResp;
import com.easyink.wecom.domain.dto.app.WePermanentCodeResp;
import com.easyink.wecom.domain.dto.app.WePreAuthCodeResp;
import com.easyink.wecom.domain.vo.WePreAuthCodeVO;
import com.easyink.wecom.domain.vo.WePreLoginParamVO;
import com.easyink.wecom.domain.vo.WeServerTypeVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 类名: We3rdAppServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-08 16:37
 */
@Service
@Slf4j
public class We3rdAppServiceImpl implements We3rdAppService {

    private final We3rdAppClient we3rdAppClient;
    private final We3rdUserClient we3rdUserClient;
    private final RedisCache redisCache;
    private final WeAuthCorpInfoService weAuthCorpInfoService;
    private final WeAuthCorpInfoExtendService weAuthCorpInfoExtendService;
    private final RuoYiConfig ruoYiConfig;
    private final WeInitService weInitService;
    @Autowired
    private WeCorpAccountService weCorpAccountService;

    @Autowired
    public We3rdAppServiceImpl(We3rdAppClient we3rdAppClient, We3rdUserClient we3rdUserClient, RedisCache redisCache, WeAuthCorpInfoService weAuthCorpInfoService, WeAuthCorpInfoExtendService weAuthCorpInfoExtendService, RuoYiConfig ruoYiConfig, WeInitService weInitService) {
        this.we3rdAppClient = we3rdAppClient;
        this.we3rdUserClient = we3rdUserClient;
        this.redisCache = redisCache;
        this.weAuthCorpInfoService = weAuthCorpInfoService;
        this.weAuthCorpInfoExtendService = weAuthCorpInfoExtendService;
        this.ruoYiConfig = ruoYiConfig;
        this.weInitService = weInitService;
    }

    /**
     * 获取预授权码
     *
     * @return WePreAuthCodeVO
     */
    @Override
    public WePreAuthCodeVO getPreAuthCode() {
        //获取预授权码
        WePreAuthCodeResp wePreAuthCodeResp = we3rdAppClient.getPreAuthCode(ruoYiConfig.getProvider().getWebSuite().getSuiteId());
        return new WePreAuthCodeVO(wePreAuthCodeResp.getPreAuthCode(), ruoYiConfig.getProvider().getWebSuite().getSuiteId());
    }

    /**
     * 获取预登录参数
     *
     * @return WePreLoginParamVO
     */
    @Override
    public WePreLoginParamVO getPreLoginParam() {
        return new WePreLoginParamVO(ruoYiConfig.getProvider().getCorpId());
    }

    /**
     * 处理永久授权码
     *
     * @param authCode 临时授权码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePermanentCode(String authCode, String suiteId) {
        if (StringUtils.isBlank(authCode)) {
            throw new WeComException("临时授权码不合法");
        }
        //如果三方应用ID为空，则使用当前默认的服务商配置应用ID
        if (StringUtils.isBlank(suiteId)) {
            suiteId = ruoYiConfig.getProvider().getWebSuite().getSuiteId();
        }

        WePermanentCodeResp wePermanentCodeResp = we3rdAppClient.getPermanentCodeInfo(authCode, suiteId);

        if (wePermanentCodeResp == null) {
            log.warn("获取永久授权码失败,临时授权码:{}", authCode);
            return;
        }

        //缓存企业access token
        Long expiresIn = wePermanentCodeResp.getExpiresIn() == null ? 0L : wePermanentCodeResp.getExpiresIn();
        String accessToken = StringUtils.isBlank(wePermanentCodeResp.getAccessToken()) ? "" : wePermanentCodeResp.getAccessToken();
        String corpId = ObjectUtils.isEmpty(wePermanentCodeResp.getAuthCorpInfo())
                || StringUtils.isBlank(wePermanentCodeResp.getAuthCorpInfo().getCorpId()) ? "" : wePermanentCodeResp.getAuthCorpInfo().getCorpId();
        if (StringUtils.isNoneBlank(accessToken, corpId)) {
            String redisKey = WeConstans.WE_AUTH_CORP_ACCESS_TOKEN + wePermanentCodeResp.getAuthCorpInfo().getCorpId();
            redisCache.setCacheObject(redisKey, accessToken, expiresIn.intValue(), TimeUnit.SECONDS);
        }

        //保存授权企业信息
        WeAuthCorpInfo weAuthCorpInfo = new WeAuthCorpInfo(wePermanentCodeResp);
        weAuthCorpInfo.setSuiteId(suiteId);
        weAuthCorpInfo.setCancelAuth(false);
        weAuthCorpInfoService.saveOrUpdate(weAuthCorpInfo);

        //保存授权企业扩展信息
        WeAuthCorpInfoExtend weAuthCorpInfoExtend = new WeAuthCorpInfoExtend(wePermanentCodeResp);
        weAuthCorpInfoExtend.setSuiteId(suiteId);
        weAuthCorpInfoExtendService.saveOrUpdate(weAuthCorpInfoExtend);

        log.info("新增授权企业({}):{}", wePermanentCodeResp.getAuthCorpInfo().getCorpId(), weAuthCorpInfo.getCorpName());
        //企业配置初始化
        weCorpAccountService.delWeCorpAccount(wePermanentCodeResp.getAuthCorpInfo().getCorpId());
        boolean isCustomApp = weAuthCorpInfoExtend.getIsCustomizedApp() != null ? weAuthCorpInfoExtend.getIsCustomizedApp() : Boolean.FALSE;
        weInitService.initCorpConfig(corpId, weAuthCorpInfo.getCorpName(), isCustomApp);

        //刷新loginUser ->weUser -> corpId
        handlerLoginUser(wePermanentCodeResp.getAuthCorpInfo().getCorpId());
    }

    /**
     * 刷新loginUser ->weUser -> corpId
     *
     * @param corpId 企业id明文
     */
    private void handlerLoginUser(String corpId) {
        if (corpId.startsWith("wpI")) {
            return;
        }
        //明文->密文
        ToOpenCorpIdResp openCorpIdResp = we3rdUserClient.toOpenCorpid(corpId);
        String openCorpId = openCorpIdResp.getOpenCorpId();
        //通过密文获取所有loginUser
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        for (String key : keys) {
            LoginUser loginUser = redisCache.getCacheObject(key);
            if (openCorpId.equals(loginUser.getCorpId())) {
                //修改loginUser corpId->明文  externalId ->密文
                WeUser weUser = loginUser.getWeUser();
                weUser.setCorpId(corpId);
                weUser.setExternalCorpId(openCorpId);
                loginUser.setWeUser(weUser);
                //授权同步用户
                LoginTokenService.refreshWeUser(loginUser);
            }
        }

    }

    /**
     * 取消授权
     *
     * @param authCorpId 授权方企业的corpid
     * @param suiteId    第三方应用的SuiteId
     */
    @Override
    public void cancelAuth(String authCorpId, String suiteId) {
        if (StringUtils.isAnyBlank(authCorpId, suiteId)) {
            log.error("收到授权取消事件，参数缺失,authCorpId:{},suiteId:{}", authCorpId, suiteId);
            return;
        }
        log.warn("企业取消授权,authCorpId:{},suiteId:{}", authCorpId, suiteId);

        LambdaUpdateWrapper<WeAuthCorpInfo> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(WeAuthCorpInfo::getCancelAuth, true)
                .eq(WeAuthCorpInfo::getCorpId, authCorpId)
                .eq(WeAuthCorpInfo::getSuiteId, suiteId);
        weAuthCorpInfoService.update(queryWrapper);

        weCorpAccountService.delWeCorpAccount(authCorpId);
        //清缓存
        redisCache.deleteObject(WeConstans.WE_CORP_ACCOUNT + authCorpId);

        log.info("企业取消授权处理完成,authCorpId:{},suiteId:{}", authCorpId, suiteId);
    }

    /**
     * 获取服务器类型
     *
     * @return WeServerTypeVO
     */
    @Override
    public WeServerTypeVO getServerType() {
        String serverType = ruoYiConfig.getServerType();
        String internalServerType = ServerTypeEnum.INTERNAL.getType();
        String icp  =StringUtils.isBlank(ruoYiConfig.getIcp()) ? StringUtils.EMPTY : ruoYiConfig.getIcp() ;
        if (StringUtils.isBlank(serverType)) {
            return new WeServerTypeVO(internalServerType,icp);
        }
        return new WeServerTypeVO(serverType,icp);
    }

    @Override
    public Map<String, Integer> getAdminList(String corpId, String suiteId) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(suiteId)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<WeAuthCorpInfoExtend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeAuthCorpInfoExtend::getCorpId, corpId)
                .eq(WeAuthCorpInfoExtend::getSuiteId, suiteId);
        queryWrapper.last(GenConstants.LIMIT_1);
        WeAuthCorpInfoExtend weAuthCorpInfoExtend = weAuthCorpInfoExtendService.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(weAuthCorpInfoExtend) || StringUtils.isBlank(weAuthCorpInfoExtend.getAgentid())) {
            return new HashMap<>();
        }

        WeAdminListResp weAdminListResp = we3rdAppClient.getAdminList(weAuthCorpInfoExtend.getCorpId(), weAuthCorpInfoExtend.getAgentid(), ruoYiConfig.getProvider().getWebSuite().getSuiteId());

        return weAdminListResp.getAdmin().stream().collect(Collectors.toMap(WeAdminListResp.Admin::getUserid, WeAdminListResp.Admin::getAuthType));
    }

    @Override
    public Map<String, Integer> getAdminList(String corpId) {
        return getAdminList(corpId, ruoYiConfig.getProvider().getWebSuite().getSuiteId());
    }


    /**
     * 处理永久授权码
     *
     * @param authCode 临时授权码
     * @param suiteId  第三方应用的SuiteId
     */
    @Override
    public void resetPermanentCode(String authCode, String suiteId) {
        if (StringUtils.isBlank(authCode)) {
            throw new WeComException("临时授权码不合法");
        }
        //如果三方应用ID为空，则使用当前默认的服务商配置应用ID
        if (StringUtils.isBlank(suiteId)) {
            suiteId = ruoYiConfig.getProvider().getWebSuite().getSuiteId();
        }

        WePermanentCodeResp wePermanentCodeResp = we3rdAppClient.getPermanentCodeInfo(authCode, suiteId);

        if (wePermanentCodeResp == null) {
            log.warn("获取永久授权码失败,临时授权码:{}", authCode);
            return;
        }

        //缓存企业access token
        Long expiresIn = wePermanentCodeResp.getExpiresIn() == null ? 0L : wePermanentCodeResp.getExpiresIn();
        String accessToken = StringUtils.isBlank(wePermanentCodeResp.getAccessToken()) ? "" : wePermanentCodeResp.getAccessToken();
        String corpId = ObjectUtils.isEmpty(wePermanentCodeResp.getAuthCorpInfo())
                || StringUtils.isBlank(wePermanentCodeResp.getAuthCorpInfo().getCorpId()) ? "" : wePermanentCodeResp.getAuthCorpInfo().getCorpId();
        if (StringUtils.isNoneBlank(accessToken, corpId)) {
            String redisKey = WeConstans.WE_AUTH_CORP_ACCESS_TOKEN + wePermanentCodeResp.getAuthCorpInfo().getCorpId();
            redisCache.setCacheObject(redisKey, accessToken, expiresIn.intValue(), TimeUnit.SECONDS);
        }

        //保存授权企业信息
        WeAuthCorpInfo weAuthCorpInfo = new WeAuthCorpInfo(wePermanentCodeResp);
        weAuthCorpInfo.setSuiteId(suiteId);
        weAuthCorpInfo.setCancelAuth(false);
        weAuthCorpInfoService.saveOrUpdate(weAuthCorpInfo);

        //保存授权企业扩展信息
        WeAuthCorpInfoExtend weAuthCorpInfoExtend = new WeAuthCorpInfoExtend(wePermanentCodeResp);
        weAuthCorpInfoExtend.setSuiteId(suiteId);
        weAuthCorpInfoExtendService.saveOrUpdate(weAuthCorpInfoExtend);

        log.info("授权企业重置授权应用Secret({}):{}", wePermanentCodeResp.getAuthCorpInfo().getCorpId(), wePermanentCodeResp.getAuthCorpInfo().getCorpFullName());

    }
}
