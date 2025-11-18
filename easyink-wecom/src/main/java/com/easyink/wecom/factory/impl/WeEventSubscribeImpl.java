package com.easyink.wecom.factory.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeUserClient;
import com.easyink.wecom.domain.dto.WeUserDTO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 类名: WeEventSubscribeImpl
 *
 * @author: 1*+
 * @date: 2021-12-29 13:56
 */
@Service("subscribe")
@Slf4j
public class WeEventSubscribeImpl implements WeCallBackEventFactory {
    private final WeAuthCorpInfoExtendService weAuthCorpInfoExtendService;
    private final RedisCache redisCache;
    private final WeCorpAccountService weCorpAccountService;
    private final WeUserService weUserService;
    private final WeCustomerService weCustomerService;
    private final WeTagGroupService weTagGroupService;
    private final WeGroupService weGroupService;
    private final WeDepartmentService weDepartmentService;
    private final WeUserClient weUserClient;
    private final RuoYiConfig ruoYiConfig;

    @Autowired
    public WeEventSubscribeImpl(WeAuthCorpInfoExtendService weAuthCorpInfoExtendService, RedisCache redisCache, WeCorpAccountService weCorpAccountService, WeUserService weUserService, WeCustomerService weCustomerService, WeTagGroupService weTagGroupService, WeGroupService weGroupService, WeDepartmentService weDepartmentService, WeUserClient weUserClient, RuoYiConfig ruoYiConfig) {
        this.weAuthCorpInfoExtendService = weAuthCorpInfoExtendService;
        this.redisCache = redisCache;
        this.weCorpAccountService = weCorpAccountService;
        this.weUserService = weUserService;
        this.weCustomerService = weCustomerService;
        this.weTagGroupService = weTagGroupService;
        this.weGroupService = weGroupService;
        this.weDepartmentService = weDepartmentService;
        this.weUserClient = weUserClient;
        this.ruoYiConfig = ruoYiConfig;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (redisCache.addLock(getSubscribeKey(message.getAgentId(), message.getToUserName()), message.getToUserName(), WeConstans.SUBSCRIBE_EXPIRE_TIME)) {
            log.info("订阅消息:{}", JSON.toJSONString(message));
            WeCorpAccount weCorpAccount = weCorpAccountService.getOne(new LambdaQueryWrapper<WeCorpAccount>()
                    .eq(WeCorpAccount::getCorpId, message.getToUserName())
                    .eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                    .last(GenConstants.LIMIT_1)
            );
            if (weAuthCorpInfoExtendService.isCustomizedApp(message.getToUserName()) && Constants.NOT_START_CODE.equals(weCorpAccount.getStatus())) {
                weCorpAccountService.startVailWeCorpAccount(message.getToUserName());
                //同步成员客户群等
                syncMethod(message.getToUserName());

            }
        }
        if(ruoYiConfig.isInternalServer()) {
            // 自建应用更新新增的订阅员工信息
            WeUserDTO resp = weUserClient.getUserByUserId(message.getFromUserName(), message.getToUserName());
            WeUser weUser = resp.transferToWeUser();
            weUser.setCorpId(message.getToUserName());
            weUserService.insertWeUserNoToWeCom(weUser);
        }
    }

    private String getSubscribeKey(Integer agentId, String corpId) {
        return WeConstans.SUBSCRIBE_KEY + agentId + corpId;
    }

    /**
     * 更新待开发企业所有线上员工token
     *
     * @param corpId 企业id
     */
    private void refreshDkLoginToken(String corpId) {
        //获取所有企业token
        Collection<String> keys = redisCache.scans(Constants.LOGIN_TOKEN_KEY + "*");
        WeCorpAccount validWeCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        for (String key : keys) {
            LoginUser loginUser = redisCache.getCacheObject(key);
            if (validWeCorpAccount.getExternalCorpId().equals(loginUser.getWeUser().getExternalCorpId()) || validWeCorpAccount.getExternalCorpId().equals(loginUser.getCorpId())) {
                WeUser weUser = loginUser.getWeUser();
                //建立映射关系 重新设置WeUser
                log.info("重新设置用户,{},{}", weUser.getExternalUserId(), weUser.getExternalCorpId());
                WeUser buildWeUserMapping = weUserService.getWeUserByExternalMapping(weUser.getExternalUserId(), weUser.getExternalCorpId());
                if (buildWeUserMapping != null) {
                    loginUser.setWeUser(buildWeUserMapping);
                    //更新令牌
                    this.refreshToken(loginUser);
                } else {
                    log.warn("查找不到映射关系");
                }
            }
        }
    }

    private void syncMethod(String corpId) {
        weDepartmentService.synchWeDepartment(corpId);
        weUserService.syncWeUser(corpId);
        //更新token缓存
        refreshDkLoginToken(corpId);
        weTagGroupService.synchWeTags(corpId);
        weGroupService.syncWeGroup(corpId);
        weCustomerService.syncWeCustomerV2(corpId);
    }

    /**
     * 更新token
     *
     * @param loginUser 登录用户
     */
    private void refreshToken(LoginUser loginUser) {
        //反射调用接口
        try {
            Object tokenService = SpringUtils.getBean("tokenService");
            tokenService.getClass().getDeclaredMethod("refreshToken", LoginUser.class).invoke(tokenService, loginUser);
        } catch (Exception e) {
            log.error("同步部门:刷新登录用户的数据权限范围失败,e:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
