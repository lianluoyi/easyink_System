package com.easyink.wecom.service;

import com.easyink.common.core.domain.entity.WeCorpAccount;

/**
 * 类名: WeAccessTokenService
 *
 * @author: 1*+
 * @date: 2021-09-13 10:49
 */
public interface WeAccessTokenService {

    String findCommonAccessToken(String corpId);

    /**
     * 获取通讯录凭证
     *
     * @return AccessToken
     */
    String findContactAccessToken(String corpId);

    /**
     * 获取服务商凭证
     *
     * @return AccessToken
     */
    String findProviderAccessToken();

    /**
     * 获取会话存档凭证
     * @return AccessToken
     */
    String findChatAccessToken(String corpId);

    /**
     * 获取SuiteAccessToken
     *
     * @return AccessToken
     */
    String findSuiteAccessToken(String suiteId);

    /**
     * 获取三方应用授权企业凭证
     *
     * @param corpId 企业id
     * @return AccessToken
     */
    String find3rdAppCorpAuthAccessToken(String corpId);

    /**
     * 获取自建/代开发应用凭证
     *
     * @param agentId 自建/代开发应用ID
     * @param corpId  企业ID
     * @return AccessToken
     */
    String findInternalAppAccessToken(String agentId, String corpId);


    void removeToken(WeCorpAccount wxCorpAccount);
}