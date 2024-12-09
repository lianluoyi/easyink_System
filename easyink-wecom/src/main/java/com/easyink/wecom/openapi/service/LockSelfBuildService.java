package com.easyink.wecom.openapi.service;

import com.easyink.wecom.openapi.dto.ConfigCorpSelfBuildDTO;

/**
 * lock自建应用配置
 * @author tigger
 * 2024/11/19 16:22
 **/
public interface LockSelfBuildService {
    /**
     * 配置自建应用信息
     * @param selfBuildDTO
     */
    void config(ConfigCorpSelfBuildDTO selfBuildDTO);

    /**
     * 保存配置
     * @param encryptCorpId 加密企业id
     * @param decryptExternalUserIdUrl 自建应用解密外部联系人id请求url
     * @param decryptUserIdUrl 自建应用解密员工d请求url
     */
    void saveSelfConfig(String encryptCorpId, String decryptExternalUserIdUrl, String decryptUserIdUrl);
}
