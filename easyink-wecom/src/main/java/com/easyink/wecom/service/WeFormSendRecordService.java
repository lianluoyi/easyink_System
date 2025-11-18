package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeFormSendRecord;
import com.easyink.wecom.domain.enums.form.SendSourceTypeEnum;
import com.easyink.wecom.domain.model.customer.CustomerId;

import java.util.List;

/**
 * 满意度表单发送记录Service接口
 *
 * @author easyink
 * @date 2024-01-01
 */
public interface WeFormSendRecordService extends IService<WeFormSendRecord> {

    /**
     * 记录表单发送
     *
     * @param customerId 客户id
     * @param fromId 表单ID
     * @return 记录ID
     */
    Long saveFormRecord(CustomerId customerId, Long fromId);

    /**
     * 记录表单发送（包含state）
     *
     * @param customerId 客户id
     * @param fromId 表单ID
     * @param state 客户专属活码state
     * @return 记录ID
     */
    Long saveFormRecord(CustomerId customerId, Long fromId, String state);


    /**
     * 记录表单提交
     *
     * @param customerId     企业ID
     * @param formId         表单ID
     * @param formResultData
     * @return 更新结果
     */
    Long recordFormSubmit(CustomerId customerId, Long formId, String formResultData);

    /**
     * 获取超时未提交的记录
     *
     * @param corpId 企业ID
     * @param timeoutHours 超时小时数
     * @return 超时记录列表
     */
    List<WeFormSendRecord> getTimeoutRecords(String corpId, Integer timeoutHours);

    /**
     * 更新推送状态
     *
     * @param recordId 记录ID
     * @param pushStatus 推送状态
     * @return 更新结果
     */
    boolean updatePushStatus(Long recordId, Integer pushStatus);

    /**
     * 批量更新超时推送状态
     *
     * @param recordIds 记录ID列表
     * @param timeoutPushStatus 超时推送状态
     * @return 更新结果
     */
    boolean batchUpdateTimeoutPushStatus(List<Long> recordIds, Integer timeoutPushStatus);

    /**
     * 根据客户和表单获取记录
     *
     * @param customerId 客户ID
     * @param formId 表单ID
     * @return 记录
     */
    WeFormSendRecord getByCustomerAndForm(CustomerId customerId, Long formId);

    /**
     * 批量记录表单发送（包含推送开关检查）
     *
     * @param customerId 客户id
     * @param formIds 表单ID列表
     * @return 记录ID列表
     */
    List<Long> batchSaveFormRecord(CustomerId customerId, List<Long> formIds, SendSourceTypeEnum sourceTypeEnum);

    /**
     * 批量记录表单发送（包含推送开关检查和state）
     *
     * @param customerId 客户id
     * @param formIds 表单ID列表
     * @param sourceTypeEnum 发送源类型
     * @param state 客户专属活码state
     * @return 记录ID列表
     */
    List<Long> batchSaveFormRecord(CustomerId customerId, List<Long> formIds, SendSourceTypeEnum sourceTypeEnum, String state);

    /**
     * 根据表单查询state
     *
     * @param customerId
     * @param formId     表单id
     * @return
     */
    String getStateByFormId(CustomerId customerId, Long formId);
}
