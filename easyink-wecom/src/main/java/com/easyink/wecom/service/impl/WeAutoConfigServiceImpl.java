package com.easyink.wecom.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.ThirdDefaultDomainConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.client.We3rdUserClient;
import com.easyink.wecom.client.WeAdminClient;
import com.easyink.wecom.domain.WeAuthCorpInfoExtend;
import com.easyink.wecom.domain.dto.AutoConfigDTO;
import com.easyink.wecom.domain.dto.app.ToOpenCorpIdResp;
import com.easyink.wecom.domain.dto.app.UserIdToOpenUserIdResp;
import com.easyink.wecom.domain.dto.autoconfig.*;
import com.easyink.wecom.domain.req.GetDepartMemberReq;
import com.easyink.wecom.domain.resp.GetDepartMemberResp;
import com.easyink.wecom.domain.vo.WeAdminQrcodeVO;
import com.easyink.wecom.domain.vo.WeCheckQrcodeVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeAuthCorpInfoExtendService;
import com.easyink.wecom.service.WeAutoConfigService;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 类名: 企业微信后台自动化配置接口整合
 *
 * @author: 1*+
 * @date: 2021-08-23$ 11:02$
 */
@Slf4j
@Service
public class WeAutoConfigServiceImpl implements WeAutoConfigService {

    private final WeAdminClient weAdminClient;
    private final WeCorpAccountService weCorpAccountService;
    private final WeInitService weInitService;
    private final RuoYiConfig ruoYiConfig;
    private final RedisCache redisCache;
    private final WeAuthCorpInfoExtendService weAuthCorpInfoExtendService;
    private final We3rdUserClient we3rdUserClient;
    private final WeUserMapper weUserMapper;
    /**
     * 检验扫码状态
     */
    private static final String SUCC_STATUS = "QRCODE_SCAN_SUCC";
    private static final String NEVER_STATUS = "QRCODE_SCAN_NEVER";
    private static final String EPXIRE_STATUS = "QRCODE_EXPIRE";
    private static final String LOIN_FAIL = "QRCODE_LOGIN_FAIL";
    private static final String CORP_MISMATCH = "QRCODE_LOGIN_FAIL_CORP_MISMATCH";
    private static final String NEED_MOBILE_CONFIRM = "NEED_MOBILE_CONFIRM";
    private static final String WAIT_MOBILE_CONFIRM = "WAIT_MOBILE_CONFIRM";
    /**
     * 短信验证关键词
     */
    private final static String MOBILE_CONFIRM_KEYWORD = "mobileConfirm";

    @Autowired
    public WeAutoConfigServiceImpl(WeAdminClient weAdminClient, WeCorpAccountService weCorpAccountService, WeInitService weInitService, RuoYiConfig ruoYiConfig, RedisCache redisCache, WeAuthCorpInfoExtendService weAuthCorpInfoExtendService, We3rdUserClient we3rdUserClient, WeUserMapper weUserMapper) {
        this.weAdminClient = weAdminClient;
        this.weCorpAccountService = weCorpAccountService;
        this.weInitService = weInitService;
        this.ruoYiConfig = ruoYiConfig;
        this.redisCache = redisCache;
        this.weAuthCorpInfoExtendService = weAuthCorpInfoExtendService;
        this.we3rdUserClient = we3rdUserClient;
        this.weUserMapper = weUserMapper;
    }

    private static final String LOGO_IMAGE = "http://p.qlogo.cn/bizmail/x97MZCe5cW6YFTArfIPz866l1fyhwUb8lzxZDGYEy7l9nZF8v1hxEg/0";
    private static final String LOGIN_TYPE = "login_admin";
    private static final String WE_COM_SYSTEM_DOMAIN = "${weComSystemDomain}";


    /**
     * 获取企业微信后台管理界面登录二维码
     *
     * @return WeAdminQrcodeVO
     */
    @Override
    public WeAdminQrcodeVO getAdminQrcode() {
        String callback = "wwqrloginCallback_" + System.currentTimeMillis();
        String redirect = "https://work.weixin.qq.com/wework_admin/loginpage_wx?_r=989&url_hash=";
        BaseAdminResult<WeGetKeyResp> weGetKeyResp = weAdminClient.getKey(LOGIN_TYPE, callback, redirect, 1);
        if (weGetKeyResp == null || weGetKeyResp.getData() == null || StringUtils.isEmpty(weGetKeyResp.getData()
                                                                                                      .getQrcode_key())) {
            throw new WeComException("获取二维码失败!");
        }
        String qrcodeUrl = "https://work.weixin.qq.com/wework_admin/wwqrlogin/mng/qrcode?qrcode_key=%s&login_type=login_admin";
        qrcodeUrl = String.format(qrcodeUrl, weGetKeyResp.getData()
                                                         .getQrcode_key());
        return new WeAdminQrcodeVO(qrcodeUrl, weGetKeyResp.getData()
                                                          .getQrcode_key());
    }

    /**
     * 检测二维码
     *
     * @param qrcodeKey 二维码key
     * @param status    二维码状态
     * @param loginUser 当前登录人的企业ID
     * @return {@link WeCheckQrcodeVO}
     */
    @Override
    public WeCheckQrcodeVO check(String qrcodeKey, String status, LoginUser loginUser) {
        Integer qrcodeExpire = -31024;
        String resultStr = "result";
        String errCode = "errCode";
        String dataStr = "data";
        String statusStr = "status";
        String authCodeStr = "auth_code";
        String authSourceStr = "auth_source";
        JSONObject resp;
        if (redisCache.getCacheObject(confirmRedisKey(qrcodeKey)) != null) {
            // 如果该qrcodeKey的二维码还待验证,则返回待验证
            return new WeCheckQrcodeVO(WAIT_MOBILE_CONFIRM);
        }
        try {
            resp = weAdminClient.check(qrcodeKey, status);
        } catch (Exception e) {
            //check 失败不中断，等待下次执行成功
            return new WeCheckQrcodeVO(NEVER_STATUS);
        }
        JSONObject result = resp.getJSONObject(resultStr);
        if (result != null && qrcodeExpire.equals(result.getInteger(errCode))) {
            return new WeCheckQrcodeVO(EPXIRE_STATUS);
        }
        JSONObject data = resp.getJSONObject(dataStr);
        if (data == null) {
            return new WeCheckQrcodeVO(NEVER_STATUS);
        }
        String qrcodeStatus = data.getString(statusStr);
        String code = data.getString(authCodeStr);
        String source = data.getString(authSourceStr);
        if (SUCC_STATUS.equals(qrcodeStatus)) {
            try {
                String weLoginRespStr = weAdminClient.login("", code, 1, qrcodeKey, source);
                // 先判断是否需要短信验证
                if (needMobileConfirm(weLoginRespStr)) {
                    WeConfirmMobileRsp mobileRsp = filterConfirmMobile(weLoginRespStr);
                    if (mobileRsp == null || StringUtils.isBlank(mobileRsp.getTlKey())) {
                        return new WeCheckQrcodeVO(qrcodeStatus);
                    }
                    log.info("[checkQrCode]需要短信验证,截取到的tlKEy:{}", mobileRsp);
                    // 根据该qrcodeKey锁720s,不再处理前端发起的其他check请求
                    redisCache.addLock(confirmRedisKey(qrcodeKey), mobileRsp.getTlKey(), 720L);
                    return new WeCheckQrcodeVO(NEED_MOBILE_CONFIRM, mobileRsp.getTlKey(), mobileRsp.getMobile());
                }
                WeLoginResp weLoginResp = loginFilter(weLoginRespStr);
                if (weLoginResp == null) {
                    return new WeCheckQrcodeVO(qrcodeStatus);
                }
                // 处理登录后返回的页面
                handleLoginResult(weLoginResp, loginUser, qrcodeKey);
            } catch (CustomException e) {
                qrcodeStatus = CORP_MISMATCH;
            } catch (Exception e) {
                qrcodeStatus = LOIN_FAIL;
                log.error("login error:{}", ExceptionUtils.getStackTrace(e));
            }
        }
        return new WeCheckQrcodeVO(qrcodeStatus);
    }

    /**
     * 获取短信验证的rediskey ,如果需要短信验证则需要根据该key锁一定的时间,不再处理前端发起的check接口
     *
     * @param qrcodeKey qrcodeKey
     * @return 短信验证锁的key
     */
    private String confirmRedisKey(String qrcodeKey) {
        return "ADMIN_SCAN_MOBILE_CONFIRM:" + qrcodeKey;
    }


    /**
     * 判断是否验证码错误
     *
     * @param weLoginRespStr 验证返回的信息
     * @return true 验证码错误
     */
    private boolean isErrorCaptcha(String weLoginRespStr) {
        if (StringUtils.isBlank(weLoginRespStr)) {
            return false;
        }
        String errMsg = "短信验证码错误";
        return weLoginRespStr.contains(errMsg);
    }


    /**
     * 处理登录成功返回的页面
     *
     * @param weLoginResp 登录成功响应
     * @param loginUser   后台登录用户
     * @param qrcodeKey   qrCodeKey
     */
    public void handleLoginResult(WeLoginResp weLoginResp, LoginUser loginUser, String qrcodeKey) {
        //不是代开发自建应用且扫码登录成功时就初始化企业配置
        if (ruoYiConfig.isInternalServer()) {
            weInitService.initCorpConfigSynchronization(weLoginResp.getEncodeCorpId(), weLoginResp.getCorpAlias());
        }
        //缓存qrcode对应的企业ID
        String qrcodeRedisKey = "QRCODE_KEY:" + qrcodeKey;
        redisCache.setCacheObject(qrcodeRedisKey, weLoginResp, 20, TimeUnit.MINUTES);
        //三方应用登录的人和后台登录企业不一致则认为登录失败
        thirdServerCheckCorpMatch(loginUser, weLoginResp);
    }


    /**
     * 截取 tlKey(短信验证需要参数)
     *
     * @param weLoginRespStr 扫码登录返回的页面标签元素
     * @return 短信验证需要的tlKey
     */
    private String getTlKey(String weLoginRespStr) {
        String tlKey = "tl_key=";
        if (StringUtils.isBlank(weLoginRespStr) || !weLoginRespStr.contains(tlKey)) {
            return weLoginRespStr;
        }
        String content = "";
        try {
            content = weLoginRespStr.substring(weLoginRespStr.indexOf(tlKey), weLoginRespStr.length());
            content = content.substring(tlKey.length(), content.indexOf("\""));
        } catch (Exception e) {
            log.error("[checkQrCode]需要短信验证,截取tkKey异常,str:{}.,e:{}", weLoginRespStr, ExceptionUtils.getStackTrace(e));
        }
        return content;
    }


    /**
     * 截取手机号
     *
     * @param weLoginRespStr 登录接口返回信息
     * @return 手机号
     */
    private WeConfirmMobileRsp filterConfirmMobile(String weLoginRespStr) {
        try {
            String locationStr = "window.settings =";
            //截取window.settings = 后的字符串
            weLoginRespStr = weLoginRespStr.substring(weLoginRespStr.indexOf(locationStr) + locationStr.length());
            //把换行符前及前面的分号一起去除 只保留目标的json字符串
            weLoginRespStr = weLoginRespStr.substring(0, weLoginRespStr.indexOf("</script>"));
            return JSON.parseObject(weLoginRespStr, WeConfirmMobileRsp.class);
        } catch (Exception e) {
            log.info("解析登录返回字符串失败:{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }


    /**
     * 判断是否需要短信验证
     *
     * @param weLoginRespStr 返回的页面
     * @return true 是 false 否
     */
    private boolean needMobileConfirm(String weLoginRespStr) {
        if (StringUtils.isBlank(weLoginRespStr)) {
            return false;
        }
        // 如果需要短信验证 返回的页面信息里会带有  <script type="text/javascript" src="//wwcdn.weixin.qq.com/node/wwmng/wwmng/js/vue_dev_webpack/mobileConfirm/mobileConfirm$63a4faa7.js"></script>
        return weLoginRespStr.contains(MOBILE_CONFIRM_KEYWORD);
    }


    /**
     * 自动化配置
     *
     * @param autoConfigDTO autoConfigDTO
     * @param loginUser     企业ID
     */
    @Override
    public void autoConfig(AutoConfigDTO autoConfigDTO, LoginUser loginUser) {
        try {
            //如果是三方应用且corpId为空则需要抛异常
            if (StringUtils.isBlank(loginUser.getCorpId()) && ruoYiConfig.isThirdServer()) {
                throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
            }
            //获取qrcodekey对应的二维码的登录的corpid
            String qrcodeRedisKey = "QRCODE_KEY:" + autoConfigDTO.getQrcodeKey();
            WeLoginResp loginCorpCache = redisCache.getCacheObject(qrcodeRedisKey);

            WeCorpAccount weCorpAccount = getCorpAccount(loginUser, loginCorpCache);
            String oldCorpAccountCorpId = weCorpAccount.getCorpId();
            if (ruoYiConfig.isInternalServer() && loginCorpCache != null && !StringUtils.equals(loginCorpCache.getEncodeCorpId(), weCorpAccount.getCorpId())) {
                //如果qrcodekey对应的登录信息中企业id不一致，代表从A企业换成B企业，所以将企业配置进行更新
                weCorpAccount.setCorpId(loginCorpCache.getEncodeCorpId());
                weCorpAccount.setCompanyName(loginCorpCache.getCorpAlias());
            }

            initApplicationHandler(weCorpAccount, autoConfigDTO);
            //更新配置
            if (ruoYiConfig.isThirdServer() && loginUser.getWeUser() != null && StringUtils.isNotBlank(loginUser.getWeUser()
                                                                                                                .getExternalCorpId()) && StringUtils.isBlank(weCorpAccount.getExternalCorpId())) {
                weCorpAccount.setExternalCorpId(loginUser.getWeUser()
                                                         .getExternalCorpId());
            }
            weCorpAccountService.updateWeCorpAccount(weCorpAccount, oldCorpAccountCorpId);
        } catch (CustomException we) {
            throw we;
        } catch (Exception e) {
            //因为这个是通过接口分析，所以不知道会抛出什么异常，使用最大的异常进行捕获
            log.error("自动配置出现异常:{}", ExceptionUtils.getStackTrace(e));
            throw new WeComException("自动配置失败");
        }
    }

    /**
     * 获取三方应用服务器默认配置域名
     *
     * @return {@link ThirdDefaultDomainConfig}
     */
    @Override
    public ThirdDefaultDomainConfig getThirdDefaultDomainConfig() {
        if (ruoYiConfig.isInternalServer()) {
            throw new CustomException(ResultTip.TIP_SERVER_NOT_SUPPORT_INTERFACE);
        }
        return ruoYiConfig.getThirdDefaultDomain();
    }

    @Override
    public void confirmMobileCaptcha(String captcha, String tlKey, String qrKey) {
        if (StringUtils.isAnyBlank(captcha, tlKey, qrKey)) {
            throw new CustomException(ResultTip.TIP_PARAM_NAME_MISSING);
        }
        if (StringUtils.isAnyBlank(captcha, tlKey, qrKey)) {
            throw new CustomException(ResultTip.TIP_NO_CAPTCHA_OR_TLKEY);
        }
        String referer = genReferrerUrl(tlKey);
        String WeConfirmCaptchaStr = weAdminClient.confirmCaptcha(captcha, tlKey, qrKey, referer);
        if (isErrorCaptcha(WeConfirmCaptchaStr)) {
            throw new CustomException(ResultTip.TIP_ERROR_CAPTCHA);
        }
        String weLoginRespStr = weAdminClient.chooseCorp(tlKey, qrKey, referer);
        WeLoginResp weLoginResp = loginFilter(weLoginRespStr);
        if (weLoginResp == null) {
            throw new CustomException(ResultTip.TIP_CONFIRM_CAPTCHA);
        }
        // 处理登录后返回的页面
        LoginUser loginUser = LoginTokenService.getLoginUser();
        handleLoginResult(weLoginResp, loginUser, qrKey);
    }

    @Override
    public void sendCaptcha(String tlKey, String qrcodeKey) {
        if (StringUtils.isBlank(tlKey)) {
            throw new CustomException(ResultTip.TIP_PARAM_NAME_MISSING);
        }
        // 调用发送验证码接口
        weAdminClient.sendCaptcha(tlKey, qrcodeKey, genReferrerUrl(tlKey));
    }

    /**
     * 生成验证短信和发送短信验证的referrrerUrl
     *
     * @param tlKey tlkey 登录接口返回
     * @return
     */
    private String genReferrerUrl(String tlKey) {
        if (StringUtils.isBlank(tlKey)) {
            return StringUtils.EMPTY;
        }
        String s = "https://work.weixin.qq.com/wework_admin/mobile_confirm/captcha_page?tl_key=TLKEY&redirect_url=https%3A%2F%2Fwork.weixin.qq.com%2Fwework_admin%2Flogin%2Fchoose_corp%3Ftl_key%3DTLKEY&from=spamcheck";
        String referer = s.replaceAll("TLKEY", tlKey);
        return referer;
    }


    /**
     * 初始化内部应用业务处理
     *
     * @param weCorpAccount 企业配置
     * @param autoConfigDTO 自动化配置参数
     */
    private void initApplicationHandler(WeCorpAccount weCorpAccount, AutoConfigDTO autoConfigDTO) {
        if (weAuthCorpInfoExtendService.isCustomizedApp(weCorpAccount.getCorpId())) {
            WeAuthCorpInfoExtend weAuthCorpInfoExtend = weAuthCorpInfoExtendService.getOne(weCorpAccount.getCorpId(), ruoYiConfig.getProvider()
                                                                                                                                 .getDkSuite()
                                                                                                                                 .getDkId());
            if (weAuthCorpInfoExtend != null && StringUtils.isNotBlank(weAuthCorpInfoExtend.getAgentid())) {
                //1.获取应用数据
                InitApplicationModel initApplicationModel = getDkApplication(autoConfigDTO.getQrcodeKey(), weAuthCorpInfoExtend.getAgentid());
                //2.配置聊天工具栏
                log.info("企业ID({}):{}，代开发应用配置侧边栏", weCorpAccount.getCorpId(), weCorpAccount.getCompanyName());
                setSidebarConfig(autoConfigDTO.getSidebarDomain(), initApplicationModel.getCustomAgentApp().getApp_id(), weCorpAccount.getCorpId(), autoConfigDTO.getQrcodeKey());

                //3.配置客户权限
                configCustomerPermission(autoConfigDTO.getQrcodeKey(), initApplicationModel.getRootDeptId());
            } else {
                throw new CustomException(ResultTip.TIP_NOT_AUTH_DK_CORP);
            }
        } else {
            //代码中所有的休眠都是为了防止请求过快
            String qrCodeKey = autoConfigDTO.getQrcodeKey();
            //1.应用处理
            InitApplicationModel initApplicationModel = applicationHandler(qrCodeKey);
            //2.发送Secret给操作人
            sendSecretToOperator(initApplicationModel, qrCodeKey);
            //3.将创建的应用的appOpenId设置到应用ID
            weCorpAccount.setAgentId(String.valueOf(initApplicationModel.getCustomAgentApp().getApp_open_id()));
            //4.设置自建应用可调用外部联系人
            setCustomAgentAppCanUseCustomApp(initApplicationModel.getCustomAgentApp().getApp_id(), initApplicationModel.getCustomApp().getApp_open_id(), autoConfigDTO.getQrcodeKey(), initApplicationModel.getRootDeptId());
            //5.配置自建应用
            configCustomAgentApp(weCorpAccount, initApplicationModel.getCustomAgentApp().getApp_id(), initApplicationModel.getCustomAgentApp().getApp_flag(), autoConfigDTO);
        }
    }


    /**
     * 应用处理
     * 1.获取企业通讯录和客户联系应用ID、根部门ID
     * 2.删除同名的自建应用
     * 3.创建自建应用
     *
     * @param qrCodeKey 二维码key
     * @return {@link InitApplicationModel}
     */
    private InitApplicationModel applicationHandler(String qrCodeKey) {
        BaseAdminResult<WeCorpApplicationResp> corpApplication = weAdminClient.getCorpApplication(0, qrCodeKey);
        if (corpApplication.getData() == null) {
            Integer outSessionCode = -3;
            if (outSessionCode.equals(corpApplication.getResult().getErrCode())) {
                throw new WeComException("登录失败");
            } else {
                throw new WeComException(corpApplication.getResult().getMessage());
            }
        }
        List<WeCorpApplicationResp.OpenapiApp> openapiAppList = corpApplication.getData().getOpenapi_app();
        //企业微信外部联系人应用默认的openId
        Integer customAppOpenId = 2000003;
        //企业微信通讯录应用默认的openId
        Integer mailListAppOpenId = 2000002;

        InitApplicationModel initApplicationModel = new InitApplicationModel();

        for (WeCorpApplicationResp.OpenapiApp openapiApp : openapiAppList) {
            if (mailListAppOpenId.equals(openapiApp.getApp_open_id())) {
                initApplicationModel.setContactApp(openapiApp);
                contactAppHandler(openapiApp, qrCodeKey);
            }
            if (customAppOpenId.equals(openapiApp.getApp_open_id())) {
                //找到外部联系人应用的appOpenId:2000003
                customAppOpenId = openapiApp.getApp_open_id();
                //获取到根部门的ID,用于设置可见范围
                if (CollectionUtils.isNotEmpty(openapiApp.getApp_perm().getPartyids())) {
                    initApplicationModel.setRootDeptId(openapiApp.getApp_perm().getPartyids().get(0));
                }
                initApplicationModel.setCustomApp(openapiApp);
            }
            if (ruoYiConfig.getDefaultAppName().equals(openapiApp.getName())) {
                //拿到我们自己自建应用并删除
                weAdminClient.delOpenApiApp(openapiApp.getApp_id(), openapiApp.getApp_open_id(), "APP_TYPE_MSG", qrCodeKey);
            }
        }
        ThreadUtil.sleep(300);
        WeCorpApplicationResp.OpenapiApp customAgentApp = createOpenApiApp(initApplicationModel.getRootDeptId(), qrCodeKey);
        initApplicationModel.setCustomAgentApp(customAgentApp);
        ThreadUtil.sleep(300);
        return initApplicationModel;
    }

    /**
     * 获取代开发应用
     *
     * @param qrCodeKey 二维码key
     * @param agentId   应用ID
     * @return {@link InitApplicationModel}
     */
    private InitApplicationModel getDkApplication(String qrCodeKey, String agentId) {
        BaseAdminResult<WeCorpApplicationResp> corpApplication = weAdminClient.getCorpApplication(0, qrCodeKey);
        if (corpApplication.getData() == null) {
            Integer outSessionCode = -3;
            if (outSessionCode.equals(corpApplication.getResult().getErrCode())) {
                throw new WeComException("登录失败");
            } else {
                throw new WeComException(corpApplication.getResult().getMessage());
            }
        }
        List<WeCorpApplicationResp.OpenapiApp> openapiAppList = corpApplication.getData().getOpenapi_app();

        InitApplicationModel initApplicationModel = new InitApplicationModel();

        for (WeCorpApplicationResp.OpenapiApp openapiApp : openapiAppList) {
            if (StringUtils.equals(String.valueOf(openapiApp.getApp_open_id()), agentId)) {
                initApplicationModel.setCustomAgentApp(openapiApp);
            }
            //企业微信外部联系人应用默认的openId
            Integer customAppOpenId = 2000003;
            if (customAppOpenId.equals(openapiApp.getApp_open_id())) {
                //获取到根部门的ID,用于设置可见范围
                if (CollectionUtils.isNotEmpty(openapiApp.getApp_perm().getPartyids())) {
                    initApplicationModel.setRootDeptId(openapiApp.getApp_perm().getPartyids().get(0));
                }
            }
        }

        return initApplicationModel;
    }

    /**
     * 发送Secert给操作人
     * 按照自建应用 ->　通讯录　-> 客户顺序给操作人发送Secert
     *
     * @param initApplicationModel {@link InitApplicationModel}
     */
    private void sendSecretToOperator(InitApplicationModel initApplicationModel, String qrCodeKey) {
        int businessType = 1;
        int appType = 1;
        if (initApplicationModel.getCustomAgentApp() != null && StringUtils.isNotBlank(initApplicationModel.getCustomAgentApp().getApp_id())) {
            weAdminClient.create(initApplicationModel.getCustomAgentApp().getApp_id(), businessType, appType, qrCodeKey);
            ThreadUtil.sleep(300);
        }
        if (initApplicationModel.getContactApp() != null && StringUtils.isNotBlank(initApplicationModel.getContactApp().getApp_id())) {
            weAdminClient.create(initApplicationModel.getContactApp().getApp_id(), businessType, appType, qrCodeKey);
            ThreadUtil.sleep(300);
        }
        if (initApplicationModel.getCustomApp() != null && StringUtils.isNotBlank(initApplicationModel.getCustomApp().getApp_id())) {
            weAdminClient.create(initApplicationModel.getCustomApp().getApp_id(), businessType, appType, qrCodeKey);
            ThreadUtil.sleep(300);
        }
    }

    /**
     * 创建自建应用
     *
     * @param rootDeptId 根部门ID
     * @param qrCodeKey  二维码Kye
     * @return {@link WeCorpApplicationResp.OpenapiApp}
     */
    private WeCorpApplicationResp.OpenapiApp createOpenApiApp(String rootDeptId, String qrCodeKey) {
        log.info("开始创建「{}」应用", ruoYiConfig.getDefaultAppName());
        BaseAdminResult<WeCorpApplicationResp.OpenapiApp> result = weAdminClient.addOpenApiApp(ruoYiConfig.getDefaultAppName(), ruoYiConfig.getDefaultAppName(), "", "", true, LOGO_IMAGE, rootDeptId, qrCodeKey);
        if (result.getData() == null) {
            throw new WeComException("应用创建失败");
        }
        WeCorpApplicationResp.OpenapiApp easyWecomApp = result.getData();
        if (StringUtils.isBlank(easyWecomApp.getApp_id())) {
            throw new WeComException("应用参数缺失");
        }
        if (easyWecomApp.getApp_open_id() == null || easyWecomApp.getApp_flag() == null) {
            throw new WeComException("应用参数缺失");
        }
        return easyWecomApp;
    }

    /**
     * 设置「开启自建应用可调用客户联系」
     *
     * @param appId           应用ID
     * @param customOpenAppId 客户联系ID
     * @param qrCodeKey       二维码Key
     * @param rootDeptId      根部门ID
     */
    private void setCustomAgentAppCanUseCustomApp(String appId, Integer customOpenAppId, String qrCodeKey, String rootDeptId) {
        ThreadUtil.sleep(300);
        BaseAdminResult<WeApiAccessibleAppsResp> weApiAccessibleAppsRespBaseAdminResult = weAdminClient.getApiAccessibleApps(customOpenAppId, qrCodeKey);
        WeApiAccessibleAppsResp weApiAccessibleAppsResp = weApiAccessibleAppsRespBaseAdminResult.getData();
        if (weApiAccessibleAppsResp == null) {
            throw new WeComException("获取允许调用客户功能应用列表失败");
        }
        //如果已经设置了，就不再设置了
        if (weApiAccessibleAppsResp.getAuth_list().getAppid_list().contains(appId)) {
            log.info("应用已设置可调用外部联系人，不再重复设置");
            return;
        }
        //这边先只设置自己
        ThreadUtil.sleep(300);
        weAdminClient.setApiAccessibleApps(customOpenAppId, appId, qrCodeKey);
        //配置客户权限
        configCustomerPermission(qrCodeKey, rootDeptId);
    }

    /**
     * 配置客户权限范围
     *
     * @param qrCodeKey  二维码key0
     * @param rootDeptId 根部门
     */
    private void configCustomerPermission(String qrCodeKey, String rootDeptId) {
        BaseAdminResult<WeGetGroupListResp> result = weAdminClient.getGroupList(qrCodeKey);

        if (result.getData() != null) {
            //当权限配置——客户联系和客户群的使用范围为空时才进行配置
            if (result.getData().getFollower_datas() != null
                    && CollectionUtils.isEmpty(result.getData().getFollower_datas().getParties())
                    && CollectionUtils.isEmpty(result.getData().getFollower_datas().getMembers())
            ) {
                weAdminClient.followers(rootDeptId, qrCodeKey);
            }
            //当权限配置——客户朋友圈的使用范围为空时才进行配置
            if (result.getData().getMomentsFollower_datas() != null
                    && CollectionUtils.isEmpty(result.getData().getMomentsFollower_datas().getParties())
                    && CollectionUtils.isEmpty(result.getData().getMomentsFollower_datas().getMembers())
            ) {
                weAdminClient.setMomentsRange(rootDeptId, qrCodeKey);
            }
        }
    }

    /**
     * 配置自建应用
     * 1.配置接收消息回调
     * 2.配置企业微信授权登录
     * 3.配置网页授权及JS-SDK
     * 4.配置聊天工具栏
     *
     * @param weCorpAccount 企业配置
     * @param appId         应用ID
     * @param appFlag       应用标识
     * @param autoConfigDTO 域名配置
     */
    private void configCustomAgentApp(WeCorpAccount weCorpAccount, String appId, Integer appFlag, AutoConfigDTO autoConfigDTO) {
        String h5Domain = autoConfigDTO.getSidebarDomain();
        //保存配置到数据库
        weCorpAccount.setH5DoMainName(h5Domain);
        if (StringUtils.isEmpty(appId)) {
            log.error("没有appId，不进行应用回调配置");
            throw new WeComException("没有appId，自动配置失败");
        }

        //1.配置接收消息回调
        String callbackUrl = WE_COM_SYSTEM_DOMAIN + "/wecom/callback/recive";
        callbackUrl = callbackUrl.replace(WE_COM_SYSTEM_DOMAIN, autoConfigDTO.getWeComSystemDomain());
        String callbackDomain = replaceDomain(autoConfigDTO.getWeComSystemDomain());
        callbackDomain = callbackDomain.split(":")[0];
        //检测回调URL
        String pass = "PASS";
        ThreadUtil.sleep(300);
        String verifyUrl = replaceDomain(h5Domain);
        // 非代开发应用才需要配置后端回调
        if (ruoYiConfig.isInternalServer()) {
            log.info("开始配置后端回调");
            BaseAdminResult<WeCheckCustomAppUrlResp> respBaseAdminResult = weAdminClient.checkCustomAppURL(callbackUrl, appId, "url", autoConfigDTO.getQrcodeKey());
            if (pass.equals(respBaseAdminResult.getData().getStatus())) {
                //设置回调URL
                ThreadUtil.sleep(300);
                BaseAdminResult result = weAdminClient.saveOpenApiApp(callbackUrl, weCorpAccount.getToken(), weCorpAccount.getEncodingAesKey(), true, true, appId, callbackDomain, 0, false, autoConfigDTO.getQrcodeKey());
                if (result.getResult() != null && result.getResult().getErrCode() < 0) {
                    throw new WeComException(result.getResult().getErrCode(), result.getResult().getMessage());
                }
            } else {
                log.error("回调配置设置失败;{}", respBaseAdminResult.getData().getStatus());
                throw new CustomException(ResultTip.TIP_ILLEGAL_DOMAIN);
            }
            //保存应用回调
            weCorpAccount.setCallbackUri(callbackUrl);

            //2.配置企业微信授权登录
            ThreadUtil.sleep(300);
            log.info("开始配置登录回调");
            String loginCallbackUrl = autoConfigDTO.getDashboardDomain();
            //替换到协议前缀，最后防止后缀多了个斜杠 最终结果： 域名 + 端口
            loginCallbackUrl = replaceDomain(loginCallbackUrl);
            BaseAdminResult setLoginCallbackResult = weAdminClient.saveOpenApiApp(loginCallbackUrl, "", "", "", appId, appFlag, false, false, autoConfigDTO.getQrcodeKey());
            if (setLoginCallbackResult.getResult() != null && setLoginCallbackResult.getResult().getErrCode() < 0) {
                throw new WeComException(setLoginCallbackResult.getResult().getErrCode(), setLoginCallbackResult.getResult().getMessage());
            }
            //保存登录回调
            weCorpAccount.setWxQrLoginRedirectUri(loginCallbackUrl);

            //3.配置网页授权及JS-SDK
            ThreadUtil.sleep(300);
            log.info("开始配置JS-SDK回调");
            downloadDomainVerifyInfo(autoConfigDTO.getWeComSystemDomain(), weCorpAccount, autoConfigDTO.getQrcodeKey());
            //替换到协议前缀，最后防止后缀多了个斜杠 最终结果： 域名 + 端口
            BaseAdminResult setJsResult = weAdminClient.saveOpenApiApp(appId, verifyUrl, false, verifyUrl, true, autoConfigDTO.getQrcodeKey());
            if (setJsResult.getResult() != null && setJsResult.getResult().getErrCode() < 0) {
                throw new WeComException(setJsResult.getResult().getErrCode(), setJsResult.getResult().getMessage());
            }
        }
        //4.配置聊天工具栏
        log.info("开始配置侧边栏");
        setSidebarConfig(autoConfigDTO.getSidebarDomain(), appId, weCorpAccount.getCorpId(), autoConfigDTO.getQrcodeKey());
    }

    /**
     * 替换域名
     *
     * @param domain 域名
     * @return 去除协议前缀的域名
     */
    private String replaceDomain(String domain) {
        String str = domain.replace("http://", "").replace("https://", "").replace("/", "");
        if (StringUtils.isBlank(str)) {
            throw new WeComException("domain不合法");
        }
        return str;
    }

    /**
     * 替换域名
     *
     * @param domain 域名
     * @return 去除协议后面'/'域名
     */
    private String replaceDomainSign(String domain) {
        String str = domain.replace("/", "");
        if (StringUtils.isBlank(str)) {
            throw new WeComException("domain不合法");
        }
        return str;
    }

    /**
     * 配置侧边栏的客户画像和素材库
     *
     * @param verifyUrl 可信域名
     * @param appId     应用ID
     * @param corpId    企业ID
     * @param qrCodeKey 二维码key
     */
    private void setSidebarConfig(String verifyUrl, String appId, String corpId, String qrCodeKey) {
        //用于判断是否已经设置过,防止重复设置
        String chatName = "素材库";
        String portraitName = "客户画像";
        String applicationName = "应用工具";
        boolean needChat = true;
        boolean needPortrait = true;
        boolean needApplication = true;


        ThreadUtil.sleep(300);
        try {
            BaseAdminResult<WeGetChatMenuResp> getChatMenuRespBaseAdminResult = weAdminClient.getChatMenu(qrCodeKey);
            List<WeGetChatMenuResp.ChatMenu> menuList = getChatMenuRespBaseAdminResult.getData().getPublish_banner_block();
            for (WeGetChatMenuResp.ChatMenu menu : menuList) {
                if (chatName.equals(menu.getItem_name()) && menu.getCorp_app() != null && StringUtils.equals(menu.getCorp_app().getApp_id(), appId)) {
                    needChat = false;
                }
                if (portraitName.equals(menu.getItem_name()) && menu.getCorp_app() != null && StringUtils.equals(menu.getCorp_app().getApp_id(), appId)) {
                    needPortrait = false;
                }
                if (applicationName.equals(menu.getItem_name()) && menu.getCorp_app() != null && StringUtils.equals(menu.getCorp_app().getApp_id(), appId)) {
                    needApplication = false;
                }
                if (!needChat && !needPortrait && !needApplication) {
                    break;
                }
            }
        } catch (Exception e) {
            //这边的判断不影响下面的增加，主要是上面都把旧应用删除，每次都相当于一个新应用去配置
            log.error("获取应用侧边栏菜单异常:{}", ExceptionUtils.getStackTrace(e));
        }
        String corpIdSign = "${corpId}";
        String protocolSign = "${protocol}";
        String portSign = "${port}";
        String hostSign = "${host}";
        String stateSign = "${state}";
        // 去掉url协议后的"//"
        verifyUrl = replaceDomainSign(verifyUrl);
        String[] array = verifyUrl.split(":");
        //域名切分完成后应该是一个protocol 一个host + 一个port
        int arrayLength = 3;
        if (array.length != arrayLength) {
            throw new WeComException("侧边栏域名填写不正确");
        }
        if (needChat) {
            ThreadUtil.sleep(300);
            log.info("配置侧边栏素材库");
            String chatUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&redirect_uri=${protocol}%3A%2F%2F${host}%3A${port}%2F%23%2Fchat&response_type=code&scope=snsapi_base&state=${state}#wechat_redirect";
            chatUrl = chatUrl.replace(corpIdSign, corpId).replace(protocolSign, array[0]).replace(hostSign, array[1]).replace(portSign, array[2]).replace(stateSign, corpId);

            weAdminClient.addChatMenu(appId, 1, 0, 0, false, appId, LOGO_IMAGE, ruoYiConfig.getDefaultAppName(), LOGO_IMAGE, 1, chatName, chatName, chatUrl, qrCodeKey);
        } else {
            log.info("已配置过侧边栏素材库");
        }

        if (needPortrait) {
            ThreadUtil.sleep(300);
            log.info("配置侧边栏画像");
            String portraitUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&redirect_uri=${protocol}%3A%2F%2F${host}%3A${port}%2F%23%2Fportrait&response_type=code&scope=snsapi_base&state=${state}#wechat_redirect";
            //替换appid=%s(corpId) redirect_uri=http%3A%2F%2F%s(host)%3A%d(port)%2F%23%2F
            portraitUrl = portraitUrl.replace(corpIdSign, corpId).replace(protocolSign, array[0]).replace(hostSign, array[1]).replace(portSign, array[2]).replace(stateSign, corpId);
            weAdminClient.addChatMenu(appId, 1, 0, 0, false, appId, LOGO_IMAGE, ruoYiConfig.getDefaultAppName(), LOGO_IMAGE, 1, portraitName, portraitName, portraitUrl, qrCodeKey);
        } else {
            log.info("已配置过侧边栏画像");
        }

        if (needApplication) {
            ThreadUtil.sleep(300);
            log.info("配置侧边栏应用工具");
            String applicationUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corpId}&redirect_uri=${protocol}%3A%2F%2F${host}%3A${port}%2F%23%2FapplicationSet&response_type=code&scope=snsapi_base&state=${state}#wechat_redirect";
            //替换appid=%s(corpId) redirect_uri=http%3A%2F%2F%s(host)%3A%d(port)%2F%23%2F
            applicationUrl = applicationUrl.replace(corpIdSign, corpId).replace(protocolSign, array[0]).replace(hostSign, array[1]).replace(portSign, array[2]).replace(stateSign, corpId);
            weAdminClient.addChatMenu(appId, 1, 0, 0, false, appId, LOGO_IMAGE, ruoYiConfig.getDefaultAppName(), LOGO_IMAGE, 1, applicationName, applicationName, applicationUrl, qrCodeKey);
        } else {
            log.info("已配置过侧边栏应用工具");
        }
    }

    /**
     * 下载域名校验文件
     *
     * @param weCorpAccount 企业配置
     */
    private void downloadDomainVerifyInfo(String domain, WeCorpAccount weCorpAccount, String qrcodeKey) {
        log.info("开始下载域名校验文件");
        DomainOwnershipVerifyInfoResp domainOwnershipVerifyInfoResp = weAdminClient.getDomainOwnershipVerifyInfo(qrcodeKey);
        if (domainOwnershipVerifyInfoResp == null) {
            throw new WeComException("获取域名校验文件失败");
        }
        String fileName = domainOwnershipVerifyInfoResp.getFile_name();
        String fileContent = weAdminClient.getDomainOwnershipVerifyInfo("download", qrcodeKey);
        if (StringUtils.isBlank(fileContent)) {
            throw new WeComException("下载域名校验文件失败");
        }

        String filePath = RuoYiConfig.getUploadPath();
        //创建一个域名校验文件
        String url;
        File file = new File(filePath + WeConstans.SLASH + fileName);
        try {
            //判断文件夹是否存在不存在则进行创建，防止因为没有文件夹导致创建文件失败
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //如果域名校验文件不存在且创建成功则将内容写入到文件中
            if (!file.exists() && file.createNewFile()) {
                try (FileWriter fw = new FileWriter(file, true)) {
                    fw.write(fileContent);
                    fw.flush();
                }
            }
        } catch (IOException e) {
            log.error("域名文件处理出现异常;{}", ExceptionUtils.getStackTrace(e));
            throw new WeComException("域名校验文件写入失败");
        }
        //拼出域名访问文件url： 后端域名/profile/文件名
        url = domain + "/profile" + WeConstans.SLASH + fileName;
        weCorpAccount.setCertFilePath(url);
        log.info("域名校验文件:{}", url);
    }

    /**
     * 截取登录接口返回值中的用户信息
     *
     * @param pageText html网页str <xxx><xx/> window.settings = {};\n xxxx
     * @return {@link WeLoginResp}
     */
    private WeLoginResp loginFilter(String pageText) {
        try {
            String locationStr = "window.settings =";
            //截取window.settings = 后的字符串
            pageText = pageText.substring(pageText.indexOf(locationStr) + locationStr.length());
            //把换行符前及前面的分号一起去除 只保留目标的json字符串
            pageText = pageText.substring(0, pageText.indexOf('\n') - 1);
            return JSON.parseObject(pageText, WeLoginResp.class);
        } catch (Exception e) {
            log.info("解析登录返回字符串失败:{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 开启通讯录应用同步和手动编辑功能
     *
     * @param contactApp 通讯录App{@link WeCorpApplicationResp.OpenapiApp}
     * @param qrCodeKey  二维码Key
     */
    private void contactAppHandler(WeCorpApplicationResp.OpenapiApp contactApp, String qrCodeKey) {
        if (contactApp == null) {
            return;
        }
        int appOpenFlag = 0;
        if (appOpenFlag == contactApp.getApp_open()) {
            //如果通讯录API接口同步功能未开启，则调用接口开启
            BaseAdminResult baseAdminResult = weAdminClient.openMailListApi(contactApp.getApp_id(), contactApp.getApp_open_id(), Boolean.TRUE, qrCodeKey);
            if (baseAdminResult.getResult() != null && baseAdminResult.getResult().getErrCode() < 0) {
                throw new WeComException(baseAdminResult.getResult().getErrCode(), baseAdminResult.getResult().getMessage());
            }
        }
        //接口读写权限 + 手动编辑
        int openRwFlag = 1;
        BaseAdminResult baseAdminResult = weAdminClient.openMailListRWPermission(contactApp.getApp_id(), openRwFlag, Boolean.TRUE, Boolean.TRUE, qrCodeKey);
        if (baseAdminResult.getResult() != null && baseAdminResult.getResult().getErrCode() < 0) {
            throw new WeComException(baseAdminResult.getResult().getErrCode(), baseAdminResult.getResult().getMessage());
        }
        log.info("通讯录API应用开启完毕!");
    }

    /**
     * 获取企业配置信息
     *
     * @param loginUser      当前登录用户
     * @param loginCorpCache 缓存cache
     * @return {@link WeCorpAccount}
     */
    private WeCorpAccount getCorpAccount(LoginUser loginUser, WeLoginResp loginCorpCache) {
        WeCorpAccount weCorpAccount;

        if (ruoYiConfig.isInternalServer()) {
            //如果没有corpid且是内部应用才是用无参数获取企业配置的方式-> admin帐号首次登录自动化配置时会没有corpid
            if (StringUtils.isBlank(loginUser.getCorpId())) {
                weCorpAccount = weCorpAccountService.findValidWeCorpAccount();
            } else {
                weCorpAccount = weCorpAccountService.findValidWeCorpAccount(loginUser.getCorpId());
            }
        } else {
            // 由于现在只有返回加密的corpId(wpI) ,此步骤会导致corpId是明文的而获取不到,所以注释掉
//            if (StringUtils.isNotBlank(loginUser.getCorpId()) && loginUser.getCorpId().equals(loginUser.getWeUser().getExternalCorpId())) {
//                loginUser.setCorpId(loginCorpCache.getEncodeCorpId());
//            }
            weCorpAccount = weCorpAccountService.findValidWeCorpAccount(loginUser.getCorpId());
        }
        //使用自动化配置前置条件
        if (weCorpAccount == null) {
            throw new CustomException(ResultTip.TIP_NOT_AVAILABLE_CONFIG_FOUND);
        }
        //增加token补偿机制
        if (StringUtils.isBlank(weCorpAccount.getToken())) {
            weCorpAccount.setToken(ruoYiConfig.getSelfBuild().getToken());
        }
        //增加aeskey补偿机制
        if (StringUtils.isBlank(weCorpAccount.getEncodingAesKey())) {
            weCorpAccount.setEncodingAesKey(ruoYiConfig.getSelfBuild().getEncodingAesKey());
        }
        return weCorpAccount;
    }

    /**
     * 三方服务器时检测扫码登录的企业是否匹配
     *
     * @param loginUser   当前登录人
     * @param weLoginResp 扫码返回
     */
    private void thirdServerCheckCorpMatch(LoginUser loginUser, WeLoginResp weLoginResp) {
        //如果是三方应用且登录人的企业ID不为空
        if (ruoYiConfig.isThirdServer() && StringUtils.isNotBlank(loginUser.getCorpId())) {
            //如果企业ID和外部企业ID相等则只有加密的企业ID
            if (StringUtils.isNotBlank(loginUser.getWeUser().getExternalCorpId()) && loginUser.getWeUser().getExternalCorpId().startsWith("wpI") && loginUser.getCorpId().equals(loginUser.getWeUser().getExternalCorpId())) {
                //将明文的企业ID转成密文
                ToOpenCorpIdResp toOpenCorpIdResp = we3rdUserClient.toOpenCorpid(weLoginResp.getEncodeCorpId());
                String encodeCorpId = "";
                if (toOpenCorpIdResp.getErrcode() == 0) {
                    encodeCorpId = toOpenCorpIdResp.getOpenCorpId();
                }
                if (!encodeCorpId.equals(loginUser.getCorpId())) {
                    throw new CustomException(ResultTip.TIP_CORP_MISMATCH);
                }
            } else {
                //corpId是明文 ，外部企业Id是密文
                if (!loginUser.getCorpId().equals(weLoginResp.getEncodeCorpId())) {
                    //判断用户明文企业ID与扫码的企业明文ID是否相等
                    throw new CustomException(ResultTip.TIP_CORP_MISMATCH);
                }
            }
        }
    }

    @Override
    @Async
    public void getDepartMemberInfo(String corpId, String qrcodeKey) {
        if (StringUtils.isBlank(qrcodeKey)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 获取缓存的企微后台登录信息
        String qrcodeRedisKey = "QRCODE_KEY:" + qrcodeKey;
        WeLoginResp loginResp = redisCache.getCacheObject(qrcodeRedisKey);
        if(loginResp == null || StringUtils.isBlank(loginResp.getRoot_depart_id())) {
            throw new CustomException(ResultTip.TIP_NO_SCAN);
        }
        // 1.  进入企微后台拉取所有员工的隐私信息
        List<GetDepartMemberResp.MemberInfo> memberInfos = GetDepartMemberReq.getAllMember(qrcodeKey, loginResp.getRoot_depart_id());
        if (CollectionUtils.isEmpty(memberInfos)) {
            log.info("[获取部门成员详情]没有拉取到部门成员, qrcodeKey :{}", qrcodeKey);
            return;
        }
        log.info("[获取部门成员详情]本次拉取到{}个部门成员详情", memberInfos.size());
        //  2. 获取该企业员工列表，数据转换
        List<WeUser> weUserList = weUserMapper.selectWeUserList(WeUser.builder()
                                                                      .corpId(corpId)
                                                                      .build());
        if (CollectionUtils.isEmpty(weUserList)) {
            return;
        }
        List<WeUser> updateUserList;
        if (ruoYiConfig.isThirdServer()) {
            // 代开发应用处理
            updateUserList = matchUserId4DkApp(corpId, memberInfos, weUserList);
        } else {
            // 自建应用处理
            updateUserList = matchUserId4SelfBuild(memberInfos, weUserList);
        }
        log.info("[获取部门成员详情]本次更新{}个部门成员隐私信息", updateUserList.size());
        //  3.  更新数据库里的员工隐私信息
       if(CollectionUtils.isNotEmpty(updateUserList) ) {
           BatchInsertUtil.doInsert(updateUserList, weUserMapper::batchUpdateUserPrivacy);
       }
    }

    /**
     * 为自建应用员工信息匹配企微后台RPA拉取到的隐私信息
     * (如果是自建应用，直接使用接口的acctid匹配员工userid )
     *
     * @param memberInfos 企微后台拉取rpa拉取到的信息
     * @param weUserList  db中的员工信息
     * @return
     */
    private List<WeUser> matchUserId4SelfBuild(List<GetDepartMemberResp.MemberInfo> memberInfos, List<WeUser> weUserList) {
        // 生成 accid(userid) -> 成员隐私信息  映射
        Map<String, GetDepartMemberResp.MemberInfo> userIdMap = memberInfos.stream()
                                                                           .collect(Collectors.toMap(GetDepartMemberResp.MemberInfo::getAcctid, a -> a));
        for (WeUser user : weUserList) {
            if (StringUtils.isBlank(user.getUserId())) {
                continue;
            }
            if (userIdMap.containsKey(user.getUserId())) {
                // 设置隐私信息
                setPrivateInfo(user, userIdMap.get(user.getUserId()));
            }
        }
        return weUserList;
    }


    /**
     * 为代开发应用匹配 从企微后台获取到信息
     * （如果是代开发应用，优先使用username匹配员工姓名，若没有匹配到或匹配到多个，则使用企微接口提供的明文转密文接口匹配员工userid）
     *
     * @param corpId      企业id
     * @param memberInfos 企微后台拉取rpa拉取到的信息
     * @param weUserList  db中的员工信息
     * @return
     */
    private List<WeUser> matchUserId4DkApp(String corpId, List<GetDepartMemberResp.MemberInfo> memberInfos, List<WeUser> weUserList) {
        // 生成 name(username) -> 成员隐私信息列表  映射
        Map<String, List<GetDepartMemberResp.MemberInfo>> privacyUserNameMap = memberInfos.stream()
                                                                                          .collect(Collectors.groupingBy(GetDepartMemberResp.MemberInfo::getName));
        // 生成 name - > 员工实体列表的映射, 如果list<weUser> 超过两个元素,则表名有重名的员工,需要特殊处理
        Map<String, List<WeUser>> weUserNameMap = weUserList.stream()
                                                            .collect(Collectors.groupingBy(WeUser::getName));
        // 需要通过userid明文转密文的隐私信息（企业姓名重复的时候需要，大多时候不需要）
        List<GetDepartMemberResp.MemberInfo> needTransferUserIdList = new ArrayList<>();
        List<WeUser> updateUserList = new ArrayList<>();
        // 优先使用
        for (Map.Entry<String, List<WeUser>> entry : weUserNameMap.entrySet()) {
            String name = entry.getKey();
            List<WeUser> list = entry.getValue();
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            List<GetDepartMemberResp.MemberInfo> memberInfoList = privacyUserNameMap.get(name);
            if (CollectionUtils.isEmpty(memberInfoList)) {
                continue;
            }
            if (list.size() > 1) {
                // 以名字分组后 list大小大于1 ,表示存在同名的员工,存储起来后续特殊处理,调用
                needTransferUserIdList.addAll(memberInfoList);
                continue;
            }
            // 获取不重名的员工
            WeUser user = list.get(0);
            if (privacyUserNameMap.containsKey(user.getName())) {
                // 设置隐私信息
                setPrivateInfo(user, memberInfoList.get(0));
                updateUserList.add(user);
            }
        }
        // 增加重复姓名的用户隐私数据
        updateUserList.addAll(dealRepeatNamePrivacyInfo(corpId, needTransferUserIdList, weUserList));
        return updateUserList;

    }

    /**
     * 处理、获取代开发重名用户的隐私信息
     *
     * @param corpId                 企业id
     * @param needTransferUserIdList 需要代开发转换 userid 明文-》 密文的 隐私信息
     * @param weUserList             db成员实体列表
     */
    private List<WeUser> dealRepeatNamePrivacyInfo(String corpId, List<GetDepartMemberResp.MemberInfo> needTransferUserIdList, List<WeUser> weUserList) {
        if (CollectionUtils.isEmpty(needTransferUserIdList) || CollectionUtils.isEmpty(weUserList)) {
            return Collections.emptyList();
        }
        List<String> userIds = needTransferUserIdList.stream()
                                                     .map(GetDepartMemberResp.MemberInfo::getAcctid)
                                                     .collect(Collectors.toList());
        // userId 明文转密文
        UserIdToOpenUserIdResp userIdToOpenUserIdResp = we3rdUserClient.useridToOpenuserid(userIds, corpId);
        if(userIdToOpenUserIdResp == null || CollectionUtils.isEmpty(userIdToOpenUserIdResp.getOpenUserIdList())) {
            return Collections.emptyList();
        }
        List<WeUser> updateUserList = new ArrayList<>();
        // userid 明文- 》密文的映射
        Map<String,String> userIdMap = userIdToOpenUserIdResp.getOpenUserIdList().stream().collect(Collectors.toMap(
                UserIdToOpenUserIdResp.OpenUserId::getUserId , UserIdToOpenUserIdResp.OpenUserId :: getOpenUserId)) ;
        // 密文userid - > 员工实体映射
        Map<String,WeUser> weUserMap = weUserList.stream().collect(Collectors.toMap(
                WeUser::getUserId, a->a));
        needTransferUserIdList.forEach(a -> {
            if(userIdMap.containsKey(a.getAcctid())) {
                //设置成密文
                String openUserid =  userIdMap.get(a.getAcctid());
                a.setAcctid(openUserid);
                if(weUserMap.containsKey(openUserid)) {
                    WeUser user = weUserMap.get(openUserid);
                    setPrivateInfo(user, a);
                    updateUserList.add(user);
                }
            }
        });
        return updateUserList;
    }


    /**
     * 为user实体设置隐私信息
     *
     * @param user       员工实体
     * @param memberInfo 隐私信息{@link com.easyink.wecom.domain.resp.GetDepartMemberResp.MemberInfo}
     */
    private void setPrivateInfo(WeUser user, GetDepartMemberResp.MemberInfo memberInfo) {
        String defaultValue = StringUtils.EMPTY;
        user.setAvatarMediaid(StringUtils.isNotBlank(memberInfo.getImgid()) ? memberInfo.getImgid() : defaultValue);
        user.setAddress(StringUtils.isNotBlank(memberInfo.getXcx_corp_address()) ?memberInfo.getXcx_corp_address() : defaultValue);
        user.setEmail(StringUtils.isNotBlank(memberInfo.getEmail()) ? memberInfo.getEmail() : defaultValue);
        user.setMobile(StringUtils.isNotBlank(memberInfo.getMobile()) ? memberInfo.getMobile() :defaultValue);
    }

}
