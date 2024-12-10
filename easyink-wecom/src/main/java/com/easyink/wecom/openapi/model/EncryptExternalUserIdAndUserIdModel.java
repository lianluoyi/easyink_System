package com.easyink.wecom.openapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 解密外部联系人和员工id映射model
 * @author tigger
 * 2024/11/25 16:01
 **/
@Data
@NoArgsConstructor
public class EncryptExternalUserIdAndUserIdModel {

    // 员工映射 k: 明文 v: 密文
    private Map<String, String> userIdToOpenUserIdMapping = new HashMap<>();
    // 外部联系人映射 k: 明文 v: 密文
    private Map<String, String> externalUserIdToOpenExternalUserIdMapping = new HashMap<>();

    public EncryptExternalUserIdAndUserIdModel(Map<String, String> userIdMapping, Map<String, String> externalUserIdMapping) {
        this.userIdToOpenUserIdMapping = userIdMapping;
        this.externalUserIdToOpenExternalUserIdMapping = externalUserIdMapping;
    }
}
