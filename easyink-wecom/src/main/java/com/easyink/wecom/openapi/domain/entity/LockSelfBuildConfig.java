package com.easyink.wecom.openapi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * lock自建应用entity
 * @author tigger
 * 2024/11/19 16:48
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockSelfBuildConfig {

    /**
     * 加密的企业id
     */
    @TableField("encrypt_corp_id")
    private String encryptCorpId;

    /**
     * 自建应用请求url地址
     */
    @TableField("decrypt_external_userid_url")
    private String decryptExternalUserIdUrl;


    @TableField("decrypt_userid_url")
    private String decryptUserIdUrl;
}
