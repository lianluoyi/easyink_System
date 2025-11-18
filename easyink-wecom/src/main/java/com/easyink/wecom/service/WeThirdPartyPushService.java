package com.easyink.wecom.service;

import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushSysData;

/**
 * 第三方推送Service接口
 *
 * @author easyink
 * @date 2024-01-01
 */
public interface WeThirdPartyPushService {

    /**
     * 推送表单提交数据
     *
     * @param pushData 推送数据
     * @param recordId
     * @return 推送结果
     */
    boolean pushFormSubmitData(ThirdPartyPushSysData pushData, Long recordId);

    /**
     * 通用推送方法
     *
     * @param corpId 企业ID
     * @param pushData 推送数据(JSON字符串)
     * @param pushType 推送类型
     * @param recordId 记录ID
     * @param fastError 是否快速失败
     * @return 推送结果
     */
    boolean push(String corpId, String pushData, Integer pushType, Long recordId, boolean fastError);
}
