package com.easyink.wecom.service;

import java.util.Map;

/**
 * ClassName： UpdateUserIdService
 * 用来更新userId 用“,”分隔的表
 *
 * @author wx
 * @date 2022/8/23 19:22
 */
public interface UpdateUserIdService {
    /**
     * 更新weMomentTask users
     * 该表存储userId是用","分隔的所以要先提取userId再替换openUserId再拼接起来存入
     *
     * @param openUserIdMap     userId:openUserId 的键值对
     * @param corpId
     */
    void updateMomentTaskUserIds(Map<String,String> openUserIdMap, String corpId);

    /**
     * 更新we_operations_center_customer_sop_filter(users
     * 该表存储userId是用","分隔的所以要先提取userId再替换openUserId再拼接起来存入
     *
     * @param openUserIdMap     userId:openUserId 的键值对
     * @param corpId
     */
    void updateSOPFilterUserIds(Map<String,String> openUserIdMap, String corpId);

    /**
     * 更新we_customer_messageoriginal(staff_id和external_id
     *
     * @param openUserIdMap
     * @param openExternalUserIdMap
     * @param corpId
     */
    void updateCustomerOriginal(Map<String,String> openUserIdMap, Map<String, String> openExternalUserIdMap, String corpId);
}
