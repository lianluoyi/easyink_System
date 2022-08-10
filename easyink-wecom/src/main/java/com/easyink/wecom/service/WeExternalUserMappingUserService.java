package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeExternalUserMappingUser;

import java.util.List;

/**
 * 类名: WeExternalUserMappingUserService
 *
 * @author: 1*+
 * @date: 2021-11-30 14:28
 */
public interface WeExternalUserMappingUserService extends IService<WeExternalUserMappingUser> {


    /**
     * 获取映射关系
     *
     * @param externalCorpId 三方企业ID
     * @param externalUserId 三方员工ID
     * @return {@link WeExternalUserMappingUser}
     */
    WeExternalUserMappingUser getMappingByExternal(String externalCorpId, String externalUserId);

    /**
     * 获取映射关系
     *
     * @param corpId 企业ID
     * @param userId 员工ID
     * @return {@link WeExternalUserMappingUser}
     */
    WeExternalUserMappingUser getMappingByInternal(String corpId, String userId);

    /**
     * 创建映射关系
     *
     * @param corpId     企业ID
     * @param userIdList 员工列表
     */
    void createMapping(String corpId, List<String> userIdList);

    /**
     * 创建映射关系
     *
     * @param corpId 企业ID
     * @param userId 员工列表
     */
    void createMapping(String corpId, String userId);

    /**
     * 初始化某个企业的映射关系
     *
     * @param corpId 企业ID
     */
    void initMapping(String corpId);

}
