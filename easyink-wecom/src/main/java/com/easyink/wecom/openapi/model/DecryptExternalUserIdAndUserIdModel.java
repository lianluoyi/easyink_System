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
public class DecryptExternalUserIdAndUserIdModel {

    // 员工映射 k: 密文 v: 明文
    private Map<String, String> userIdMapping = new HashMap<>();
    // 外部联系人映射 k: 密文 v: 明文
    private Map<String, String> externalUserIdMapping = new HashMap<>();

    public DecryptExternalUserIdAndUserIdModel(Map<String, String> userIdMapping, Map<String, String> externalUserIdMapping) {
        this.userIdMapping = userIdMapping;
        this.externalUserIdMapping = externalUserIdMapping;
    }
}
