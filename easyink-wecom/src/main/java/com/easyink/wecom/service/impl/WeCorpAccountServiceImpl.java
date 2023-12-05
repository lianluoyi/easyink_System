package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.wecom.client.We3rdUserClient;
import com.easyink.wecom.domain.WeAuthCorpInfo;
import com.easyink.wecom.domain.WeAuthCorpInfoExtend;
import com.easyink.wecom.domain.dto.app.ToOpenCorpIdResp;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossSwitchVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCorpAccountMapper;
import com.easyink.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类名: WeCorpAccountServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-26 18:03
 */
@Slf4j
@Service("corpAccountService")
public class WeCorpAccountServiceImpl extends ServiceImpl<WeCorpAccountMapper, WeCorpAccount> implements WeCorpAccountService {


    private final WeAccessTokenService weAccessTokenService;
    private final RedisCache redisCache;
    private final WeAuthCorpInfoService weAuthCorpInfoService;
    private final RuoYiConfig ruoYiConfig;
    private final WeDepartmentService weDepartmentService;
    private final WeAuthCorpInfoExtendService weAuthCorpInfoExtendService;
    private final We3rdUserClient we3rdUserClient;
    private final WeInitService weInitService;

    @Autowired
    @Lazy
    public WeCorpAccountServiceImpl(WeAccessTokenService weAccessTokenService, RedisCache redisCache, WeAuthCorpInfoService weAuthCorpInfoService,
                                    RuoYiConfig ruoYiConfig, WeDepartmentService weDepartmentService, WeAuthCorpInfoExtendService weAuthCorpInfoExtendService,
                                    We3rdUserClient we3rdUserClient, WeInitService weInitService) {
        this.weAccessTokenService = weAccessTokenService;
        this.redisCache = redisCache;
        this.weAuthCorpInfoService = weAuthCorpInfoService;
        this.ruoYiConfig = ruoYiConfig;
        this.weDepartmentService = weDepartmentService;
        this.weAuthCorpInfoExtendService = weAuthCorpInfoExtendService;
        this.we3rdUserClient = we3rdUserClient;
        this.weInitService = weInitService;
    }


    /**
     * 修改企业配置
     *
     * @param wxCorpAccount 企业配置
     * @param corpId        企业ID
     */
    @Override
    public void updateWeCorpAccount(WeCorpAccount wxCorpAccount, String corpId) {
        boolean updateSuccess;

        //如果是内部应用服务器
        if (ruoYiConfig.isInternalServer()) {
            if (StringUtils.isNotBlank(wxCorpAccount.getCorpId())) {
                wxCorpAccount.setCorpId(wxCorpAccount.getCorpId().trim());
            }
            setTokenAesKey(wxCorpAccount);
            updateSuccess = this.updateById(wxCorpAccount);
            // 对新的corpId进行初始化,如果之前不存在对应的配置
            initIfNotExist(wxCorpAccount.getCorpId());
            // 刷新登录用户的corpId
            LoginTokenService.changeCorpId(wxCorpAccount.getCorpId());
        } else {
            //三方应用都需要使用corpid修改配置
            if (StringUtils.isBlank(corpId)) {
                throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
            }
            LambdaUpdateWrapper<WeCorpAccount> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WeCorpAccount::getCorpId, corpId);
            //如果前端传参corpid为空 或者corpid参数与登录人的corpid不一致，以登录人的corpid为准
            if (StringUtils.isBlank(wxCorpAccount.getCorpId()) || !StringUtils.equals(corpId, wxCorpAccount.getCorpId())) {
                throw new CustomException(ResultTip.TIP_CORP_MISMATCH);
            }
            //待开发应用只修改会话存档
            if (weAuthCorpInfoExtendService.isCustomizedApp(corpId)) {
                WeCorpAccount validWeCorpAccount = findValidWeCorpAccount(corpId);
                validWeCorpAccount.setChatSecret(wxCorpAccount.getChatSecret());
                // 修改流失设置开关状态
                validWeCorpAccount.setCustomerChurnNoticeSwitch(wxCorpAccount.getCustomerChurnNoticeSwitch());
                validWeCorpAccount.setCustomerLossTagSwitch(wxCorpAccount.getCustomerLossTagSwitch());
                wxCorpAccount = validWeCorpAccount;
            }
            updateSuccess = this.update(wxCorpAccount, updateWrapper);
        }
        //修改配置成功需要清除缓存的token
        if (updateSuccess) {
            weAccessTokenService.removeToken(wxCorpAccount);
            //如果是内部应用且变更了企业ID时，提出所有当前企业已登录用户
            if (ruoYiConfig.isInternalServer() && StringUtils.isNotBlank(wxCorpAccount.getCorpId()) && !StringUtils.equals(corpId, wxCorpAccount.getCorpId())) {
                //这边提出旧CorpID的用户
                LoginTokenService.forceToOffline(corpId);
                //修改当前用户为新的CorpId
                LoginTokenService.changeCorpId(wxCorpAccount.getCorpId());

            } else if (ruoYiConfig.isThirdServer()) {
                LoginTokenService.changeCorpId(wxCorpAccount.getCorpId());
            }
        }


    }


    /**
     * 修改企业配置
     *
     * @param wxCorpAccount 企业配置
     * @param loginUser     当前登录人
     */
    @Override
    public void updateWeCorpAccount(WeCorpAccount wxCorpAccount, LoginUser loginUser) {
        this.updateWeCorpAccount(wxCorpAccount, loginUser.getCorpId());
    }


    /**
     * 获取有效的企业id
     *
     * @return 结果
     */
    @Override
    public WeCorpAccount findValidWeCorpAccount() {
        WeCorpAccount weCorpAccount;
        weCorpAccount = redisCache.getCacheObject(WeConstans.WE_CORP_ACCOUNT);
        if (ObjectUtils.isNotEmpty(weCorpAccount)
                && StringUtils.isNoneBlank(weCorpAccount.getCorpId(),
                weCorpAccount.getContactSecret(),
                weCorpAccount.getAgentSecret(),
                weCorpAccount.getCorpSecret())) {
            return weCorpAccount;
        }
        weCorpAccount = this.getOne(new LambdaQueryWrapper<WeCorpAccount>()
                .eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                .eq(WeCorpAccount::getStatus, Constants.NORMAL_CODE)
                .last(GenConstants.LIMIT_1)
        );
        //关键参数不为空才缓存
        if (ObjectUtils.isNotEmpty(weCorpAccount)
                && StringUtils.isNoneBlank(weCorpAccount.getCorpId(),
                weCorpAccount.getContactSecret(),
                weCorpAccount.getAgentSecret(),
                weCorpAccount.getCorpSecret())) {
            redisCache.setCacheObject(WeConstans.WE_CORP_ACCOUNT, weCorpAccount);
        }
        return weCorpAccount;
    }


    /**
     * 启用有效的企业微信账号
     *
     * @param corpId 企业ID
     */
    @Override
    public int startVailWeCorpAccount(String corpId) {
        int returnCode = this.baseMapper.startVailWeCorpAccount(corpId);
        if (Constants.SERVICE_RETURN_SUCCESS_CODE < returnCode) {
            weAccessTokenService.removeToken(WeCorpAccount.builder().corpId(corpId).build());
        }
        return returnCode;
    }

    @Override
    public void startCustomerChurnNoticeSwitch(String corpId, String status) {
        if (StringUtils.isBlank(corpId)) {
            throw new WeComException("企业ID不能为空");
        }
        WeCorpAccount validWeCorpAccount = findValidWeCorpAccount(corpId);
        if (validWeCorpAccount == null) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 修改客户的开关
        validWeCorpAccount.setCustomerChurnNoticeSwitch(status);
        this.updateWeCorpAccount(validWeCorpAccount, corpId);
    }

    /**
     * 修改流失标签开关状态
     *
     * @param corpId 企业ID
     * @param status 开关状态
     */
    @Override
    public void startCustomerLossTagSwitch(String corpId, String status) {
        if (StringUtils.isBlank(corpId)) {
            throw new WeComException("企业ID不能为空");
        }
        WeCorpAccount validWeCorpAccount = findValidWeCorpAccount(corpId);
        if (validWeCorpAccount == null) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 修改流失标签的开关
        validWeCorpAccount.setCustomerLossTagSwitch(status);
        this.updateWeCorpAccount(validWeCorpAccount, corpId);
    }


    @Override
    public String getCustomerChurnNoticeSwitch(String corpId) {
        WeCorpAccount validWeCorpAccount = this.findValidWeCorpAccount(corpId);
        return Optional.ofNullable(validWeCorpAccount).map(WeCorpAccount::getCustomerChurnNoticeSwitch)
                .orElse(WeConstans.DEL_FOLLOW_USER_SWITCH_CLOSE);
    }

    /**
     * 查询客户流失提醒和流失标签开关状态
     *
     * @param corpId 企业ID
     * @return 结果
     */
    @Override
    public CustomerLossSwitchVO getCustomerLossSwitch(String corpId) {
        WeCorpAccount validWeCorpAccount = this.findValidWeCorpAccount(corpId);
        CustomerLossSwitchVO customerLossSwitchVO = new CustomerLossSwitchVO();
        customerLossSwitchVO.setCustomerChurnNoticeSwitch(Optional.ofNullable(validWeCorpAccount).map(WeCorpAccount::getCustomerChurnNoticeSwitch)
                .orElse(WeConstans.DEL_FOLLOW_USER_SWITCH_CLOSE));
        customerLossSwitchVO.setCustomerLossTagSwitch(Optional.ofNullable(validWeCorpAccount).map(WeCorpAccount::getCustomerLossTagSwitch)
                .orElse(WeConstans.DEL_FOLLOW_USER_SWITCH_CLOSE));
        return customerLossSwitchVO;
    }

    /**
     * 企业是否已配置内部应用
     *
     * @param corpId 授权企业ID
     * @return true：已配置内部应用，false：未配置
     */
    @Override
    public WeCorpAccount internalAppConfigured(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        LambdaQueryWrapper<WeCorpAccount> queryWrapper = new LambdaQueryWrapper<>();

        List<String> list = new ArrayList<>(2);
        list.add(Constants.NORMAL_CODE);
        list.add(Constants.NOT_START_CODE);
        queryWrapper.eq(WeCorpAccount::getExternalCorpId, corpId)
                .eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                .in(WeCorpAccount::getStatus, list)
                .last(GenConstants.LIMIT_1);

        WeCorpAccount weCorpAccount = this.getOne(queryWrapper);
        //由于现在在三方应用授权时会自动初始化corp对应的企业配置，所以判断企业是否已配置内部应用，需要增加判断密钥是否存在
        if (ObjectUtils.isNotEmpty(weCorpAccount)
                && StringUtils.isNoneBlank(weCorpAccount.getCorpId())) {
            return weCorpAccount;
        }
        return null;
    }

    /**
     * 获取授权企业配置内部应用配置
     *
     * @return List<WeCorpAccount>
     */
    @Override
    public List<WeCorpAccount> listOfAuthCorpInternalWeCorpAccount() {
        if (ruoYiConfig.isInternalServer()) {
            //如果是内部应用则直接查询WeCorpAccount中所有有效的企业配置并根据企业ID去重
            LambdaQueryWrapper<WeCorpAccount> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                    .eq(WeCorpAccount::getStatus, Constants.NORMAL_CODE)
                    .groupBy(WeCorpAccount::getCorpId);
            return this.list(queryWrapper);
        } else {
            List<WeAuthCorpInfo> authCorpInfoList = weAuthCorpInfoService.listOfAuthorizedCorpInfo(ruoYiConfig.getProvider().getWebSuite().getSuiteId());
            Set<String> corpIdSet = authCorpInfoList.stream().map(WeAuthCorpInfo::getCorpId).collect(Collectors.toSet());
            if (CollUtil.isEmpty(corpIdSet)) {
                return new ArrayList<>();
            }
            LambdaQueryWrapper<WeCorpAccount> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                    .eq(WeCorpAccount::getStatus, Constants.NORMAL_CODE)
                    .in(WeCorpAccount::getExternalCorpId, corpIdSet)
                    .groupBy(WeCorpAccount::getCorpId);
            return this.list(queryWrapper);
        }
    }

    @Override
    public String getCorpId(WeCorpAccount weCorpAccount) {
        if (ObjectUtil.isNotEmpty(weCorpAccount) && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
            return weCorpAccount.getCorpId();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 保存企业配置
     *
     * @param weCorpAccount 企业配置
     */
    @Override
    public void saveCorpConfig(WeCorpAccount weCorpAccount, LoginUser loginUser) {
        //如果是内部应用服务器
        if (ruoYiConfig.isInternalServer()) {
            // 把corpId去除空格
            if (StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                weCorpAccount.setCorpId(weCorpAccount.getCorpId().trim());
            }
            setTokenAesKey(weCorpAccount);
            //admin帐号且admin的corpId为空则直接插入
            if (loginUser.isSuperAdmin() && StringUtils.isBlank(loginUser.getCorpId())) {
                this.saveOrUpdate(weCorpAccount);
            } else {
                //否则通过corpId进行修改
                if (StringUtils.isBlank(loginUser.getCorpId())) {
                    throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
                }
                LambdaQueryWrapper<WeCorpAccount> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(WeCorpAccount::getCorpId, loginUser.getCorpId());
                this.saveOrUpdate(weCorpAccount, queryWrapper);
            }
            // 对新的corpId进行初始化,如果之前不存在对应的配置
            initIfNotExist(weCorpAccount.getCorpId());
            // 刷新登录用户的corpId
            LoginTokenService.changeCorpId(weCorpAccount.getCorpId());
        } else {
            //三方应用都需要使用corpId进行修改
            if (StringUtils.isBlank(loginUser.getCorpId())) {
                throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
            }

            LambdaUpdateWrapper<WeCorpAccount> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WeCorpAccount::getCorpId, loginUser.getCorpId());
            //是待开发则只修改会话存档
            if (weAuthCorpInfoExtendService.isCustomizedApp(loginUser.getCorpId())) {
                WeCorpAccount validWeCorpAccount = findValidWeCorpAccount(loginUser.getCorpId());
                validWeCorpAccount.setChatSecret(weCorpAccount.getChatSecret());
                weCorpAccount = validWeCorpAccount;
            }
            this.saveOrUpdate(weCorpAccount, updateWrapper);
        }
    }

    /**
     * (自建应用)设置AES_KEY 和token
     *
     * @param weCorpAccount 企业配置
     */
    private void setTokenAesKey(WeCorpAccount weCorpAccount) {
        if (weCorpAccount == null) {
            return;
        }
        weCorpAccount.setEncodingAesKey(ruoYiConfig.getSelfBuild().getEncodingAesKey());
        weCorpAccount.setToken(ruoYiConfig.getSelfBuild().getToken());
    }

    /**
     * 如果企业还未初始化,则为该corpId初始化（角色、侧边栏)
     *
     * @param corpId 企业ID
     */
    private void initIfNotExist(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        //给内部应用手动配置的用户进行初始化
        weInitService.initDefaultSystemProperty(corpId, "");
    }

    @Override
    public String getCorpName(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return StringUtils.EMPTY;
        }
        // 1. 先从授权企业信息表中获取
        WeAuthCorpInfo weAuthCorpInfo = weAuthCorpInfoService.getOne(new LambdaQueryWrapper<WeAuthCorpInfo>()
                .eq(WeAuthCorpInfo::getCorpId, corpId)
                .last(GenConstants.LIMIT_1)
        );
        if (weAuthCorpInfo != null && StringUtils.isNotBlank(weAuthCorpInfo.getCorpName())) {
            return weAuthCorpInfo.getCorpName();
        }
        // 2. 如授权信息中没有 则取该企业根部门名称
        WeDepartment department = weDepartmentService.getOne(new LambdaQueryWrapper<WeDepartment>()
                .eq(WeDepartment::getCorpId, corpId)
                .eq(WeDepartment::getId, WeConstans.WE_ROOT_DEPARMENT_ID)
                .last(GenConstants.LIMIT_1)
        );
        if (department != null && StringUtils.isNotBlank(department.getName())) {
            return department.getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getAgentId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        WeCorpAccount weCorpAccount = this.findValidWeCorpAccount(corpId);
        if (weCorpAccount == null) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return weCorpAccount.getAgentId();
    }

    /**
     * 获取url路径
     *
     * @param url 文件url
     * @param corpId 企业ID
     * @return 实际url路径
     */
    @Override
    public String getUrl(String url, String corpId) {
        if (com.easyink.common.utils.StringUtils.isAnyBlank(url, corpId)) {
            return com.easyink.common.utils.StringUtils.EMPTY;
        }
        // 获取企业配置的H5Domain域名信息
        WeCorpAccount weCorpAccount = this.findValidWeCorpAccount(corpId);
        if (url.startsWith(Constants.RESOURCE_PREFIX)) {
            // 拼接访问路径
            return weCorpAccount.getH5DoMainName() + WeConstans.SLASH + url;
        }
        return url;
    }

    /**
     * 删除企业配置
     *
     * @param corpId 企业ID
     */
    @Override
    public void delWeCorpAccount(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        //删除企业配置表中的企业配置
        this.getBaseMapper().delWeCorpAccount(corpId);
        //删除缓存的企业配置
        String redisKey = WeConstans.WE_CORP_ACCOUNT + ":" + corpId;
        redisCache.deleteObject(redisKey);
    }


    /**
     * 初始化企业配置
     *
     * @param corpId      企业ID
     * @param corpName    企业名
     * @param isCustomApp 是否代开发
     */
    @Override
    public void initWeCorpAccount(String corpId, String corpName, boolean isCustomApp) {
        if (StringUtils.isBlank(corpId)) {
            log.warn("corpId为空，不进行初始化企业配置");
            return;
        }
        //如果公司名为空则使用企业ID作为公司名
        corpName = StringUtils.isBlank(corpName) ? corpId : corpName;
        LambdaQueryWrapper<WeCorpAccount> queryWrapper = new LambdaQueryWrapper<>();
        if (ruoYiConfig.isThirdServer()) {
            //是三方应用模式下需要传corpId去查询对应的corpid配置是否已经存在
            //考虑到前端在内部应用模式下会在自动化配置或者手动配置填写三个域名后调用保存企业配置接口时插入的数据没有corpid而写的兼容
            queryWrapper.eq(WeCorpAccount::getCorpId, corpId);
        }
        queryWrapper.eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE);
        List<String> list = new ArrayList<>(2);
        list.add(Constants.NORMAL_CODE);
        list.add(Constants.NOT_START_CODE);
        queryWrapper.in(WeCorpAccount::getStatus, list);
        queryWrapper.last(GenConstants.LIMIT_1);
        WeCorpAccount weCorpAccount = this.getOne(queryWrapper);
        weCorpAccount = Optional.ofNullable(weCorpAccount).orElse(new WeCorpAccount());

        //初始化增加token和aeskey——通过secret判断是自建应用还是代开发自建
        String defaultToken = isCustomApp ? ruoYiConfig.getProvider().getDkSuite().getToken() : ruoYiConfig.getSelfBuild().getToken();
        String defaultAesKey = isCustomApp ? ruoYiConfig.getProvider().getDkSuite().getEncodingAesKey() : ruoYiConfig.getSelfBuild().getEncodingAesKey();

        //如果原corpId为空值则更新
        if (StringUtils.isBlank(weCorpAccount.getCorpId())) {
            weCorpAccount.setCorpId(corpId);
        }
        //如果公司名为空或者公司名为admin
        String oldDefaultCompanyName = "admin";
        if (StringUtils.isBlank(weCorpAccount.getCompanyName()) || oldDefaultCompanyName.equals(weCorpAccount.getCompanyName())) {
            weCorpAccount.setCompanyName(corpName);
        }
        //如果token为空则使用默认
        if (StringUtils.isBlank(weCorpAccount.getToken())) {
            weCorpAccount.setToken(defaultToken);
        }
        //如果aeskey为空则使用默认
        if (StringUtils.isBlank(weCorpAccount.getEncodingAesKey())) {
            weCorpAccount.setEncodingAesKey(defaultAesKey);
        }
        //如果代开发自建应用需要将所有密钥都设置，同时设置为授权 未启用
        if (isCustomApp) {
            WeAuthCorpInfo weAuthCorpInfo = weAuthCorpInfoService.getOne(corpId, ruoYiConfig.getProvider().getDkSuite().getDkId());
            if (weAuthCorpInfo != null) {
                weCorpAccount.setAgentSecret(weAuthCorpInfo.getPermanentCode());
                weCorpAccount.setContactSecret(weAuthCorpInfo.getPermanentCode());
                weCorpAccount.setCorpSecret(weAuthCorpInfo.getPermanentCode());
            }
            WeAuthCorpInfoExtend weAuthCorpInfoExtend = weAuthCorpInfoExtendService.getOne(corpId, ruoYiConfig.getProvider().getDkSuite().getDkId());
            if (weAuthCorpInfoExtend != null) {
                weCorpAccount.setAgentId(weAuthCorpInfoExtend.getAgentid());
            }
            try {
                ToOpenCorpIdResp result = we3rdUserClient.toOpenCorpid(corpId);
                if (result.getErrcode() == 0) {
                    weCorpAccount.setExternalCorpId(result.getOpenCorpId());
                } else {
                    weCorpAccount.setExternalCorpId("");
                }
            } catch (Exception e) {
                log.error("转换授权企业ID失败：{}", ExceptionUtils.getStackTrace(e));
            }
            weCorpAccount.setStatus(Constants.NOT_START_CODE);
            weCorpAccount.setAuthorized(Boolean.TRUE);
        }

        //三方应用增加初始化H5域名字段
        if (StringUtils.isBlank(weCorpAccount.getH5DoMainName()) && StringUtils.isNotBlank(ruoYiConfig.getThirdDefaultDomain().getSidebar())) {
            weCorpAccount.setH5DoMainName(ruoYiConfig.getThirdDefaultDomain().getSidebar());
        }
        //三方引用增加初始化回调Url字段
        if (StringUtils.isBlank(weCorpAccount.getCallbackUri()) && StringUtils.isNotBlank(ruoYiConfig.getThirdDefaultDomain().getScrm())) {
            String callbackUrl = ruoYiConfig.getThirdDefaultDomain().getScrm() + "/wecom/callback/recive";
            weCorpAccount.setCallbackUri(callbackUrl);
        }
        //初始化登录重定向URL
        if (StringUtils.isBlank(weCorpAccount.getWxQrLoginRedirectUri()) && StringUtils.isNotBlank(ruoYiConfig.getThirdDefaultDomain().getDashboard())) {
            String loginUrl = ruoYiConfig.getThirdDefaultDomain().getDashboard().replace("http://", "");
            weCorpAccount.setWxQrLoginRedirectUri(loginUrl);
        }
        this.saveOrUpdate(weCorpAccount);

    }

    /**
     * 获取有效的企业配置
     *
     * @param corpId 企业ID
     * @return WeCorpAccount
     */
    @Override
    public WeCorpAccount findValidWeCorpAccount(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            if (ruoYiConfig.isInternalServer()) {
                return this.findValidWeCorpAccount();
            } else {
                throw new WeComException("企业ID不能为空");
            }
        }
        WeCorpAccount weCorpAccount;
        String redisKey = WeConstans.WE_CORP_ACCOUNT + ":" + corpId;
        weCorpAccount = redisCache.getCacheObject(redisKey);
        if (ObjectUtils.isNotEmpty(weCorpAccount) && StringUtils.isNotBlank(weCorpAccount.getExternalCorpId())) {
            return weCorpAccount;
        }
        weCorpAccount = this.getOne(new LambdaQueryWrapper<WeCorpAccount>()
                .eq(WeCorpAccount::getCorpId, corpId)
                .eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                .and(i -> i.eq(WeCorpAccount::getStatus, Constants.NORMAL_CODE).or().eq(WeCorpAccount::getStatus, Constants.NOT_START_CODE))
                .last(GenConstants.LIMIT_1)
        );
        if (weCorpAccount == null && ruoYiConfig.isThirdServer()) {
            // 如果是代开发应用且通过明文找不到企业配置,则转成密文后再获取一次
            ToOpenCorpIdResp toOpenCorpIdResp = we3rdUserClient.toOpenCorpid(corpId);
            if (toOpenCorpIdResp.getErrcode() == 0) {
                corpId = toOpenCorpIdResp.getOpenCorpId();
            }
            weCorpAccount = this.getOne(new LambdaQueryWrapper<WeCorpAccount>()
                    .eq(WeCorpAccount::getCorpId, corpId)
                    .eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                    .and(i -> i.eq(WeCorpAccount::getStatus, Constants.NORMAL_CODE).or().eq(WeCorpAccount::getStatus, Constants.NOT_START_CODE))
                    .last(GenConstants.LIMIT_1));
        }
        if (null != weCorpAccount
                && Constants.NORMAL_CODE.equals(weCorpAccount.getStatus())
                && StringUtils.isNoneBlank(weCorpAccount.getCorpId(),
                weCorpAccount.getContactSecret(),
                weCorpAccount.getAgentSecret(),
                weCorpAccount.getCorpSecret())) {
            redisCache.setCacheObject(redisKey, weCorpAccount);
            // 如果是三方应用需要从配置文件读取侧边栏的URL
            if (ruoYiConfig.isThirdServer()) {
                weCorpAccount.setH5DoMainName(ruoYiConfig.getThirdDefaultDomain().getSidebar());
            }
        }
        return weCorpAccount;
    }

}
