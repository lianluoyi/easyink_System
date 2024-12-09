package com.easyink.wecom.openapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.openapi.constant.SelfBuildConst;
import com.easyink.wecom.openapi.dao.LockSelfBuildConfigMapper;
import com.easyink.wecom.openapi.domain.entity.LockSelfBuildConfig;
import com.easyink.wecom.openapi.dto.ConfigCorpSelfBuildDTO;
import com.easyink.wecom.openapi.res.BaseSelfBuildRes;
import com.easyink.wecom.openapi.service.LockSelfBuildService;
import com.easyink.wecom.openapi.util.OkHttpClientUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

/**
 * lock自建应用service impl
 * @author tigger
 * 2024/11/19 16:22
 **/
@Service
@Slf4j
@AllArgsConstructor
public class LockSelfBuildServiceImpl implements LockSelfBuildService {

    private final LockSelfBuildConfigMapper configMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void config(ConfigCorpSelfBuildDTO selfBuildDTO) {
        String selfBuildUrl = selfBuildDTO.getSelfBuildUrl();
        String encryptCorpId = selfBuildDTO.getEncryptCorpId();
        log.info("[lock自建应用配置] selfBuildUrl: {}, encryptCorpId: {}", selfBuildUrl, encryptCorpId);
        // 请求待开发自建应用解密externalUserId的接口uri
        String decryptExternalUserIdUrl = selfBuildUrl + SelfBuildConst.DECRYPT_EXTERNAL_USERID_REQ_URL;
        // 请求待开发自建应用解密员工userId的接口uri
        String decryptUserIdUrl = selfBuildUrl + SelfBuildConst.DECRYPT_USERID_REQ_URL;
        // 请求自建应用服务是否可用的uri
        String checkUrl = selfBuildUrl + SelfBuildConst.CHECK_URL;
        // 校验自建服务地址是否可用, 不可用返回异常
        String resultStr = OkHttpClientUtil.doPost(checkUrl, StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8));
        if (resultStr == null) {
            throw new CustomException(ResultTip.SELF_BUILD_ACCESS_DENIED_URL);
        }
        BaseSelfBuildRes res = JSON.parseObject(resultStr, BaseSelfBuildRes.class);
        if (res == null || !res.isSuccess()) {
            throw new CustomException(ResultTip.SELF_BUILD_REQ_FAILED);
        }
        this.saveSelfConfig(selfBuildDTO.getEncryptCorpId(), decryptExternalUserIdUrl, decryptUserIdUrl);
    }

    @Override
    public void saveSelfConfig(String encryptCorpId, String decryptExternalUserIdUrl, String decryptUserIdUrl) {
        if (StringUtils.isAnyBlank(encryptCorpId, decryptExternalUserIdUrl, decryptUserIdUrl)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        configMapper.insertOrUpdate(new LockSelfBuildConfig(encryptCorpId, decryptExternalUserIdUrl, decryptUserIdUrl));
    }
}
