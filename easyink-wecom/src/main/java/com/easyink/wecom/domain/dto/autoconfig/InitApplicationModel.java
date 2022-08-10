package com.easyink.wecom.domain.dto.autoconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: InitApplicationModel
 *
 * @author: 1*+
 * @date: 2021-11-10 18:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitApplicationModel {

    /**
     * 企业根部门
     */
    private String rootDeptId;
    /**
     * 客户联系应用
     */
    private WeCorpApplicationResp.OpenapiApp customApp;
    /**
     * 通讯录联系应用
     */
    private WeCorpApplicationResp.OpenapiApp contactApp;
    /**
     * 自建应用
     */
    private WeCorpApplicationResp.OpenapiApp customAgentApp;
}
