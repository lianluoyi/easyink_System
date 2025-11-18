package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.wecom.client.We3rdUserClient;
import com.easyink.wecom.domain.WeAuthCorpInfo;
import com.easyink.wecom.domain.WeExternalUserMappingUser;
import com.easyink.wecom.domain.dto.app.UserIdToOpenUserIdResp;
import com.easyink.wecom.mapper.WeExternalUserMappingUserMapper;
import com.easyink.wecom.service.WeAuthCorpInfoService;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeExternalUserMappingUserService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: WeExternalUserMappingUserServiceImpl
 *
 * @author: 1*+
 * @date: 2021-11-30 14:30
 */
@Slf4j
@Service
public class WeExternalUserMappingUserServiceImpl extends ServiceImpl<WeExternalUserMappingUserMapper, WeExternalUserMappingUser> implements WeExternalUserMappingUserService {


    private final We3rdUserClient we3rdUserClient;
    private final WeCorpAccountService weCorpAccountService;
    private final WeAuthCorpInfoService weAuthCorpInfoService;
    private final WeUserService weUserService;

    @Lazy
    @Autowired
    public WeExternalUserMappingUserServiceImpl(We3rdUserClient we3rdUserClient, WeCorpAccountService weCorpAccountService, WeAuthCorpInfoService weAuthCorpInfoService, WeUserService weUserService) {
        this.we3rdUserClient = we3rdUserClient;
        this.weCorpAccountService = weCorpAccountService;
        this.weAuthCorpInfoService = weAuthCorpInfoService;
        this.weUserService = weUserService;
    }

    /**
     * 获取映射关系
     *
     * @param externalCorpId 三方企业ID
     * @param externalUserId 三方员工ID
     * @return {@link WeExternalUserMappingUser}
     */
    @Override
    public WeExternalUserMappingUser getMappingByExternal(String externalCorpId, String externalUserId) {
        if (StringUtils.isAnyBlank(externalCorpId, externalUserId)) {
            return new WeExternalUserMappingUser();
        }

        LambdaQueryWrapper<WeExternalUserMappingUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeExternalUserMappingUser::getExternalCorpId, externalCorpId);
        queryWrapper.eq(WeExternalUserMappingUser::getExternalUserId, externalUserId);
        queryWrapper.last(GenConstants.LIMIT_1);

        return this.get(queryWrapper);
    }

    /**
     * 获取映射关系
     *
     * @param corpId 企业ID
     * @param userId 员工ID
     * @return {@link WeExternalUserMappingUser}
     */
    @Override
    public WeExternalUserMappingUser getMappingByInternal(String corpId, String userId) {
        if (StringUtils.isAnyBlank(corpId, userId)) {
            return new WeExternalUserMappingUser();
        }

        LambdaQueryWrapper<WeExternalUserMappingUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeExternalUserMappingUser::getCorpId, corpId);
        queryWrapper.eq(WeExternalUserMappingUser::getUserId, userId);
        queryWrapper.last(GenConstants.LIMIT_1);


        return this.get(queryWrapper);
    }

    /**
     * 创建映射关系
     *
     * @param corpId     企业ID
     * @param userIdList 员工列表
     */
    @Override
    public void createMapping(String corpId, List<String> userIdList) {
        try {
            if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(userIdList)) {
                return;
            }
            WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
            if (weCorpAccount == null || StringUtils.isBlank(weCorpAccount.getExternalCorpId())) {
                return;
            }
            WeAuthCorpInfo weAuthCorpInfo = weAuthCorpInfoService.getOne(weCorpAccount.getExternalCorpId());
            if (weAuthCorpInfo == null || StringUtils.isAnyBlank(weAuthCorpInfo.getCorpId(), weAuthCorpInfo.getSuiteId())) {
                return;
            }

            UserIdToOpenUserIdResp resp = we3rdUserClient.useridToOpenuserid(userIdList, weAuthCorpInfo.getCorpId());

            List<WeExternalUserMappingUser> mappingUserList = new ArrayList<>();
            for (UserIdToOpenUserIdResp.OpenUserId openUserId : resp.getOpenUserIdList()) {
                if (StringUtils.equals(openUserId.getUserId(), openUserId.getOpenUserId())) {
                    continue;
                }
                WeExternalUserMappingUser mappingUser = new WeExternalUserMappingUser();
                mappingUser.setCorpId(corpId);
                mappingUser.setExternalCorpId(weAuthCorpInfo.getCorpId());
                mappingUser.setUserId(openUserId.getUserId());
                mappingUser.setExternalUserId(openUserId.getOpenUserId());
                mappingUserList.add(mappingUser);
            }
            if (CollectionUtils.isNotEmpty(mappingUserList)) {
                baseMapper.saveOrUpdateBatch(mappingUserList);
            }
        } catch (Exception e) {
            log.error("创建映射关系出现异常，err:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 初始化某个企业的映射关系
     *
     * @param corpId 企业ID
     */
    @Override
    public void initMapping(String corpId) {
        List<String> userList = weUserService.listOfUserId(corpId, (String) null);
        if (CollectionUtils.isEmpty(userList) || !needInit(corpId, userList)) {
            return;
        }
        this.createMapping(corpId, userList);
    }

    /**
     * 创建映射关系
     *
     * @param corpId 企业ID
     * @param userId 员工列表
     */
    @Override
    public void createMapping(String corpId, String userId) {
        List<String> list = new ArrayList<>(1);
        list.add(userId);
        this.createMapping(corpId, list);
    }


    private WeExternalUserMappingUser get(LambdaQueryWrapper<WeExternalUserMappingUser> queryWrapper) {

        WeExternalUserMappingUser result = this.getOne(queryWrapper);
        if (result == null) {
            return new WeExternalUserMappingUser();
        }
        return result;
    }


    private boolean needInit(String corpId, List<String> userIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(userIdList)) {
            return false;
        }

        LambdaQueryWrapper<WeExternalUserMappingUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeExternalUserMappingUser::getCorpId, corpId);
        queryWrapper.in(WeExternalUserMappingUser::getUserId, userIdList);

        int count = (int)this.count(queryWrapper);
        return count != userIdList.size();
    }


}
