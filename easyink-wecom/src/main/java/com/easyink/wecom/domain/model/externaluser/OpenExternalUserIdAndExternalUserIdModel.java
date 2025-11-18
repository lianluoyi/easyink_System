package com.easyink.wecom.domain.model.externaluser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密文外部联系人和外部联系人model
 * @author tigger
 * 2024/12/10 18:11
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenExternalUserIdAndExternalUserIdModel {

    /**
     * 加密后的外部联系人id
     */
    private String openExternalUserId;
    /**
     * 外部联系人id
     */
    private String externalUserId;
}
