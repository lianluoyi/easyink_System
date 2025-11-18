package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleDecryptVO {

    /**
     * 解密值
     */
    private String decryptValue;
}