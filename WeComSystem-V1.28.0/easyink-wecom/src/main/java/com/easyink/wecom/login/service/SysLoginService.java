package com.easyink.wecom.login.service;

import cn.hutool.core.util.ObjectUtil;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.LoginTypeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.user.CaptchaException;
import com.easyink.common.exception.user.CaptchaExpireException;
import com.easyink.common.exception.user.QrCodeLoginException;
import com.easyink.common.exception.user.UserPasswordNotMatchException;
import com.easyink.common.manager.AsyncManager;
import com.easyink.common.manager.factory.AsyncFactory;
import com.easyink.common.token.SysPermissionService;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.MessageUtils;
import com.easyink.common.utils.ServletUtils;
import com.easyink.wecom.client.We3rdUserClient;
import com.easyink.wecom.client.WeAccessTokenClient;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.client.WeUserClient;
import com.easyink.wecom.domain.WeExternalUserMappingUser;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.mapper.WeDepartmentMapper;
import com.easyink.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.easyink.common.utils.wecom.LoginRsaUtil.decryptByPrivateKey;


/**
 * 登录校验方法
 *
 * @author admin
 */
@Slf4j
@Component
public class SysLoginService {

    @Resource
    private AuthenticationManager authenticationManager;
    private final WeUserClient weUserClient;
    private final WeUpdateIDClient weUpdateIDClient;
    private final WeAccessTokenClient weAccessTokenClient;
    private final WeUserService weUserService;
    private final SysPermissionService permissionService;
    private final TokenService tokenService;
    private final WeDepartmentMapper weDepartmentMapper;
    private final WeAuthCorpInfoService weAuthCorpInfoService;
    private final WeCorpAccountService weCorpAccountService;
    private final We3rdUserClient we3rdUserClient;
    private final RedisCache redisCache;
    private final RuoYiConfig ruoYiConfig;
    private final We3rdAppService we3rdAppService;

    @Autowired
    private WeExternalUserMappingUserService weExternalUserMappingUserService;

    @Autowired
    public SysLoginService(@NotNull WeUserClient weUserClient, WeUpdateIDClient weUpdateIDClient, @NotNull WeUserService weUserService,
                           @NotNull SysPermissionService permissionService, @NotNull TokenService tokenService,
                           @NotNull WeDepartmentMapper weDepartmentMapper, WeAccessTokenClient weAccessTokenClient, WeAuthCorpInfoService weAuthCorpInfoService, WeCorpAccountService weCorpAccountService, We3rdUserClient we3rdUserClient, RedisCache redisCache, RuoYiConfig ruoYiConfig, We3rdAppService we3rdAppService) {
        this.weUserClient = weUserClient;
        this.weUpdateIDClient = weUpdateIDClient;
        this.weUserService = weUserService;
        this.permissionService = permissionService;
        this.tokenService = tokenService;
        this.weDepartmentMapper = weDepartmentMapper;
        this.weAccessTokenClient = weAccessTokenClient;
        this.weAuthCorpInfoService = weAuthCorpInfoService;
        this.weCorpAccountService = weCorpAccountService;
        this.we3rdUserClient = we3rdUserClient;
        this.redisCache = redisCache;
        this.ruoYiConfig = ruoYiConfig;
        this.we3rdAppService = we3rdAppService;
    }

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {

        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount();
        String corpId = weCorpAccountService.getCorpId(weCorpAccount);
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"), LoginTypeEnum.BY_PASSWORD.getType()));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"), LoginTypeEnum.BY_PASSWORD.getType()));
            throw new CaptchaException();
        }

        //内部应用才支持帐号密码登录
        if (ruoYiConfig.isThirdServer()) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, username, Constants.LOGIN_FAIL, MessageUtils.message("server.not.support"), LoginTypeEnum.BY_PASSWORD.getType()));
            throw new CustomException("服务器不支持该登录方式");
        }

        // 用户验证
        Authentication authentication;
        try {
            // 解密密码
            String decryptPassword = decryptByPrivateKey(ruoYiConfig.getLoginRsaPrivateKey(), password);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, decryptPassword));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"), LoginTypeEnum.BY_PASSWORD.getType()));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, username, Constants.LOGIN_FAIL, e.getMessage(), LoginTypeEnum.BY_PASSWORD.getType()));
                throw new CustomException(ResultTip.TIP_GENERAL_ERROR, e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"), LoginTypeEnum.BY_PASSWORD.getType()));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //帐号密码登录需要获取当前启用的配置，把corpId设置到登录信息，同时考虑到未配置企业配置时
        loginUser.getUser().setCorpId(corpId);
        // 生成token
        return tokenService.createToken(loginUser);
    }


    /**
     * 第三方SCRM系统获取token
     *
     * @param corpId    企业id
     * @param userId    用户id
     * @return
     */
    public LoginResult getLoginToken(String corpId, String userId) {
        if (StringUtils.isAnyBlank(corpId, userId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        if (ruoYiConfig.isThirdServer()) {
            CorpIdToOpenCorpIdResp openCorpId = weUpdateIDClient.getOpenCorpId(corpId);
            corpId = openCorpId.getOpen_corpid();
            userId = weUserService.getOpenUserId(corpId, userId);
        }
        //  构造 登录用户实体
        WeUser weUser = weUserService.selectWeUserById(corpId, userId);
        if (weUser == null) {
            throw new CustomException(ResultTip.TIP_USER_NOT_ACTIVE);
        }
        LoginUser loginUser = new LoginUser(weUser, permissionService.getMenuPermission(weUser));
        loginUser.setIsOtherSysUse(true);
        // 缓存登录信息 生成token
        String token = tokenService.createToken(loginUser);
        Cookie cookie = new Cookie("Admin-Token", token);
        cookie.setHttpOnly(true);
        ServletUtils.getResponse().addCookie(cookie);
        return new LoginResult(token, null);
    }

    /**
     * 处理登录回调
     *
     * @param code  扫码返回code
     * @param state 附带state
     * @return token 返回给前端的授权token
     */
    public LoginResult qrCodeLogin(String code, String state) {
        log.info("扫码登录回调：code:{},state:{}", code, state);
        //三方应用该接口不允许使用
        if (ruoYiConfig.isThirdServer()) {
            throw new CustomException(ResultTip.TIP_SERVER_NOT_SUPPORT);
        }
        WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount();
        String corpId = weCorpAccountService.getCorpId(weCorpAccount);
        if (StringUtils.isBlank(code)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, Constants.QR_CODE_SCAN_USER, Constants.LOGIN_FAIL, MessageUtils.message(Constants.GET_CODE_FAIL), LoginTypeEnum.BY_SCAN.getType()));
            throw new QrCodeLoginException(Constants.GET_CODE_FAIL);
        }
        // 1. 根据CODE 调用API获取企微用户userId
        if (ObjectUtil.isEmpty(weCorpAccount) || StringUtils.isBlank(weCorpAccount.getCorpId()) || StringUtils.isBlank(weCorpAccount.getContactSecret())) {
            throw new CustomException(ResultTip.TIP_NOT_CONFIG_CONTACT);
        }
        WeUserInfoDTO weUserInfoDTO = weUserClient.getQrCodeLoginUserInfo(code, weCorpAccount.getCorpId());
        if (null == weUserInfoDTO) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, Constants.QR_CODE_SCAN_USER, Constants.LOGIN_FAIL, MessageUtils.message(Constants.GET_INFO_FAIL), LoginTypeEnum.BY_SCAN.getType()));
            throw new QrCodeLoginException(Constants.GET_INFO_FAIL);
        }
        String userId = weUserInfoDTO.getUserId();
        return loginByUserId(userId, corpId, weCorpAccount, Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * 根据userId登录
     *
     * @param userId                用户ID
     * @param corpId                企业ID（qrCodeLogin3rd传入三方企业ID，qrCodeLogin内部应用ID）
     * @param configuredInternalApp 是否配置内部应用
     * @param isThirdLogin          是否三方登陆
     * @return {@link LoginResult}
     */
    public LoginResult loginByUserId(String userId, String corpId, WeCorpAccount weCorpAccount, Boolean configuredInternalApp, Boolean isThirdLogin) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (StringUtils.isBlank(userId)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, Constants.QR_CODE_SCAN_USER, Constants.LOGIN_FAIL, MessageUtils.message(Constants.NOT_IN_COMPANY), LoginTypeEnum.BY_SCAN.getType()));
            throw new QrCodeLoginException(Constants.NOT_IN_COMPANY);
        }

        WeUser weUser;
        if (Boolean.FALSE.equals(isThirdLogin)) {
            WeUserDTO weUserDTO = weUserClient.getUserByUserId(userId, corpId);
            if (weUserDTO == null) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, Constants.QR_CODE_SCAN_USER, Constants.LOGIN_FAIL, MessageUtils.message(Constants.GET_INFO_FAIL), LoginTypeEnum.BY_SCAN.getType()));
                throw new QrCodeLoginException(Constants.GET_INFO_FAIL);
            }
            weUser = weUserDTO.transferToWeUser();
            weUser.setCorpId(corpId);
            weUserService.insertWeUserNoToWeCom(weUser);
            weUser.setDepartmentName(weDepartmentMapper.selectNameByUserId(corpId, userId));
        } else {
            weUser = this.thirdGetWeUser(userId, corpId, configuredInternalApp, weCorpAccount);
        }
        //如果官方接口获取的name为空,则从数据库中查找name插入
        if(StringUtils.isBlank(weUser.getName())){
            weUser.setName(weUserService.getUser(corpId, userId).getUserName());
        }
        // 3. 构造 登录用户实体
        LoginUser loginUser = new LoginUser(weUser, permissionService.getMenuPermission(weUser));
        if (weCorpAccount != null) {
            loginUser.setCorpName(weCorpAccount.getCompanyName());
        } else {
            loginUser.setCorpName("");
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(corpId, weUser.getName(), Constants.LOGIN_SUCCESS, MessageUtils.message(Constants.USER_LOGIN_SUCCESS), LoginTypeEnum.BY_SCAN.getType()));
        // 4. 缓存登录信息 生成token
        String token = tokenService.createToken(loginUser);
        Cookie cookie = new Cookie("Admin-Token", token);
        cookie.setHttpOnly(true);
        ServletUtils.getResponse().addCookie(cookie);
        return new LoginResult(token, loginUser);
    }


    private WeUser thirdGetWeUser(String externalUserId, String externalCorpId, Boolean configuredInternalApp, WeCorpAccount weCorpAccount) {

        //如果是三方应用则需要对userId进行转换
        if (Boolean.TRUE.equals(configuredInternalApp) && Constants.NORMAL_CODE.equals(weCorpAccount.getStatus()) && StringUtils.isNotBlank(weCorpAccount.getContactSecret())) {
            weExternalUserMappingUserService.initMapping(weCorpAccount.getCorpId());
            WeExternalUserMappingUser weExternalUserMappingUser;
            if (externalCorpId.equals(weCorpAccount.getCorpId())) {
                weExternalUserMappingUser = weExternalUserMappingUserService.getMappingByInternal(externalCorpId, externalUserId);
            } else {
                weExternalUserMappingUser = weExternalUserMappingUserService.getMappingByExternal(externalCorpId, externalUserId);
            }

            if (weExternalUserMappingUser != null && StringUtils.isNoneBlank(weExternalUserMappingUser.getUserId(), weExternalUserMappingUser.getCorpId())) {
                WeUserDTO weUserDTO = weUserClient.getUserByUserId(weExternalUserMappingUser.getUserId(), weExternalUserMappingUser.getCorpId());
                if (weUserDTO == null) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(weExternalUserMappingUser.getCorpId(), Constants.QR_CODE_SCAN_USER, Constants.LOGIN_FAIL, MessageUtils.message(Constants.GET_INFO_FAIL), LoginTypeEnum.BY_SCAN.getType()));
                    throw new QrCodeLoginException(Constants.GET_INFO_FAIL);
                }
                WeUser weUser = weUserDTO.transferToWeUser();
                weUser.setExternalCorpId(externalCorpId);
                weUser.setExternalUserId(externalUserId);
                weUser.setCorpId(weExternalUserMappingUser.getCorpId());
                weUserService.insertWeUserNoToWeCom(weUser);
                weUser.setDepartmentName(weDepartmentMapper.selectNameByUserId(weExternalUserMappingUser.getCorpId(), weExternalUserMappingUser.getUserId()));
                return weUser;
            }
        }
        WeUserDTO weUserDTO = we3rdUserClient.getUserByUserId(externalCorpId, externalUserId);
        if (weUserDTO == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(externalCorpId, Constants.QR_CODE_SCAN_USER, Constants.LOGIN_FAIL, MessageUtils.message(Constants.GET_INFO_FAIL), LoginTypeEnum.BY_SCAN.getType()));
            throw new QrCodeLoginException(Constants.GET_INFO_FAIL);
        }
        if (Boolean.FALSE.equals(configuredInternalApp)) {
            Map<String, Integer> adminMap = we3rdAppService.getAdminList(externalCorpId);
            if (ObjectUtil.isEmpty(adminMap) || !adminMap.containsKey(weUserDTO.getUserid())) {
                //如果未配置内部应用且非管理员登录提醒登录人联系人企业管理员进行内部应用配置
                throw new CustomException(ResultTip.TIP_NOT_CONFIG_CONTACT);
            }
        }
        WeUser weUser = weUserDTO.transferToWeUser();
        if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
            weUser.setCorpId(weCorpAccount.getCorpId());
        } else {
            weUser.setCorpId(externalCorpId);
        }
        weUser.setExternalCorpId(externalCorpId);
        weUser.setExternalUserId(externalUserId);
        weUserService.insertWeUserNoToWeCom(weUser);
        //三方登录默认部门名为空
        weUser.setDepartmentName("");
        return weUser;
    }

    public LoginResult loginHandler(String code, String state) {
        if (LoginTypeEnum.BY_THIRD_SCAN.getState().equals(state)||LoginTypeEnum.BY_WEB.getState().equals(state)){
            return thirdLogin(code,state);
        }else {
            return qrCodeLogin(code,state);
        }
    }

    private LoginResult thirdLogin(String code, String state){
        if (!LoginTypeEnum.BY_THIRD_SCAN.getState().equals(state) && !LoginTypeEnum.BY_WEB.getState().equals(state)){
            return null;
        }
        log.info("登录回调：code:{},state:{}", code, state);
        if (StringUtils.isBlank(code)) {
            throw new QrCodeLoginException(Constants.GET_CODE_FAIL);
        }
        WeUser weUser;
        if (LoginTypeEnum.BY_WEB.getState().equals(state)){
            weUser = webLoginHandler(code);
        }else {
            weUser = qrCode3rdLoginHandler(code);
        }
        //判断扫码人所在公司是否授权
        if (!weAuthCorpInfoService.corpAuthorized(weUser.getCorpId())) {
            //未授权
            throw new CustomException(ResultTip.TIP_NOT_AUTH_CORP);
        }
        //判断扫码人所在公司是否配置内部应用
        boolean configuredInternalApp = false;
        WeCorpAccount weCorpAccount = weCorpAccountService.internalAppConfigured(weUser.getCorpId());
        if (weCorpAccount != null) {
            configuredInternalApp = true;
        }
        //登录
        return loginByUserId(weUser.getUserId(), weUser.getCorpId(), weCorpAccount, configuredInternalApp, Boolean.TRUE);
    }

    private WeUser webLoginHandler(String code){
        // 1. 根据CODE 调用API获取企微用户userId
        WeAccessUserInfo3rdDTO weAccessUserInfo3rdDTO = we3rdUserClient.getuserinfo3rd(code);
        if (null == weAccessUserInfo3rdDTO) {
            throw new QrCodeLoginException(Constants.GET_INFO_FAIL);
        }
        return new WeUser(weAccessUserInfo3rdDTO.getCorpId(),weAccessUserInfo3rdDTO.getUserId());
    }

    private WeUser qrCode3rdLoginHandler(String code){
        // 1. 根据CODE 调用API获取企微用户userId
        WeLoginUserInfoDTO weLoginUserInfoDTO = weAccessTokenClient.getLoginInfo(code);
        if (null == weLoginUserInfoDTO) {
            throw new QrCodeLoginException(Constants.GET_INFO_FAIL);
        }
        return new WeUser(weLoginUserInfoDTO.getCorp_info().getCorpid(),weLoginUserInfoDTO.getUser_info().getUserid());
    }
}
