package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDecryptVO {
    /**
     * 加密值
     */
    private String encryptValue;
    /**
     * 解密值
     */
    private String decryptValue;

}