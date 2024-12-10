package com.easyink.wecom.openapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置企业自建应用DTO
 * @author tigger
 * 2024/11/19 15:52
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCorpSelfBuildDTO {


    /**
     * 企业id密文
     */
    private String encryptCorpId;

    /**
     * 自建应用请求主机
     */
    private String selfBuildUrl;
}
