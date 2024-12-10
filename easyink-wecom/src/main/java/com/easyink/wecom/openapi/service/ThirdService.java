package com.easyink.wecom.openapi.service;

import java.util.List;

/**
 * 第三方服务的员工service
 * @author tigger
 * 2024/11/25 17:01
 **/
public interface ThirdService {
    /**
     * 查询员工id 根据企业id
     * @param corpId 企业id
     * @return 员工id列表
     */
    List<String> listUserIdByCorpId(String corpId);

    /**
     * 根据企业id查询外部联系人id
     * @param corpId 企业id
     * @return 外部联系人id列表
     */
    List<String> listExternalUserIdByCorpId(String corpId);
}
