package com.easyink.wecom.openapi.service;

import com.easyink.wecom.openapi.res.DecryptUserIdSelfBuildRes;

import java.util.List;

/**
 * lock自建应用配置
 * @author tigger
 * 2024/11/19 16:22
 **/
public interface LockSelfBuildApiService {
    /**
     * 解密外部联系人id, 获取明文
     * 背景: 待开发应用获取的externalUserId为密文, 如需要获取对应的明文外部联系人id,  需要通过请求对应的自建应用服务, 在自建应用服务调用企微解密外部联系人接口, 获取拿到明文
     *
     * @param externalUserid 请求解密的外部联系人密文id
     * @param agentId 待开发自建应用的agentId
     * @param selfBuildUrl 企业下配置的自建应用解密url
     */
    String decryptExternalUserId(String externalUserid, String agentId, String selfBuildUrl);

    /**
     * 解密员工userid, 获取明文
     * 背景: 待开发应用获取的userId为密文, 如需要获取对应的明文员工id,  需要通过请求对应的自建应用服务, 在自建应用服务调用企微解密员工id接口, 获取拿到明文
     *
     * @param useridList 请求解密的员工userId密文id列表
     * @param agentId 待开发自建应用的agentId
     * @param selfBuildUrl 企业下配置的自建应用解密url
     */
    DecryptUserIdSelfBuildRes.GetUserIdResData decryptUserId(List<String> useridList, String agentId, String selfBuildUrl);


}
