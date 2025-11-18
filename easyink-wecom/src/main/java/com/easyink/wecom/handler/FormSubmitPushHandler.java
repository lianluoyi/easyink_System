package com.easyink.wecom.handler;

import com.alibaba.fastjson.JSON;
import com.easyink.wecom.domain.WeFormSendRecord;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushSysData;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.enums.form.push.FormRecordStatusEnum;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.service.WeCustomerExtendPropertyService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.WeFormSendRecordService;
import com.easyink.wecom.service.WeThirdPartyPushService;
import com.easyink.wecom.service.form.WeFormAdvanceSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 满意度表单提交处理器
 *
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FormSubmitPushHandler {

    private final WeFormSendRecordService formSendRecordService;
    private final WeFormAdvanceSettingService formAdvanceSettingService;
    private final WeThirdPartyPushService thirdPartyPushService;
    private final WeCustomerExtendPropertyService weCustomerExtendPropertyService;
    private final WeCustomerService customerService;

    /**
     * 处理表单提交
     *
     * @param customerId 客户ID
     * @param formId         表单ID
     * @param formResultData 表单结果数据
     * @return 处理结果
     */
    public boolean handleFormSubmit(CustomerId customerId, Long formId, String formResultData) {
        if (customerId.invalid() || formId == null) {
            log.warn("[表单提交推送处理] 处理表单提交失败：参数不完整");
            return false;
        }

        try {
            log.info("[表单提交推送处理] 开始处理表单提交推送 customerId={}, formId={}", customerId, formId);

            // 1. 记录表单提交
            Long recordId = formSendRecordService.recordFormSubmit(customerId, formId, formResultData);
            if (recordId ==  null) {
                log.warn("[表单提交推送处理] 表单发送记录更新提交状态失败 customerId={}, formId={}", customerId, formId);
                return false;
            }

            // 2. 检查是否需要推送
            WeFormAdvanceSetting formAdvanceSetting = formAdvanceSettingService.getById(formId);
            if (formAdvanceSetting == null || !isPushEnabled(formAdvanceSetting.getPushContentFlag())) {
                log.info("[表单提交推送处理] 表单未配置推送或推送开关已关闭 customerId={}, formId={}", customerId, formId);
                return true;
            }

            // 3. 构建推送数据
            ThirdPartyPushSysData pushData = buildSubmitFormPushData(customerId, formId, formResultData, formAdvanceSetting);
            if (pushData == null) {
                log.warn("[表单提交推送处理] 构建推送数据失败 customerId={}, formId={}", customerId, formId);
                return false;
            }
            // 4. 异步推送数据
            thirdPartyPushService.pushFormSubmitData(pushData, recordId);

            log.info("[表单提交推送处理] 处理表单提交推送成功 customerId={}, formId={}", customerId, formId);
            return true;

        } catch (Exception e) {
            log.error("[表单提交推送处理] 处理表单提交推送异常 customerId={}, formId={}, error={}",
                    customerId, formId, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 构建推送数据
     *
     * @param customerId           客户id
     * @param formId              表单ID
     * @param formResultData      表单结果数据
     * @param formAdvanceSetting  表单高级设置
     * @return 推送数据DTO
     */
    private ThirdPartyPushSysData buildSubmitFormPushData(CustomerId customerId, Long formId,
                                                          String formResultData, WeFormAdvanceSetting formAdvanceSetting) {
        try {
            // 获取表单发送记录
            WeFormSendRecord record = formSendRecordService.getByCustomerAndForm(customerId, formId);
            if (record == null) {
                return null;
            }

            ThirdPartyPushSysData.ThirdPartyPushSysDataBuilder builder = ThirdPartyPushSysData.builder()
                    .corpId(customerId.getCorpId())
                    .pushType(FormRecordStatusEnum.PushSceneEnum.FORM_SUBMIT.getCode())
                    .pushTime(new Date());

            // 构建表单信息
            ThirdPartyPushSysData.FormInfoDTO formInfo = ThirdPartyPushSysData.FormInfoDTO.builder()
                    .formId(formId)
                    .sentTime(record.getSentTime())
                    .submitTime(record.getSubmitTime())
                    .formSubmitResult(formResultData)
                    .build();
            builder.formInfo(formInfo);

            // 构建员工信息
            ThirdPartyPushSysData.EmployeeInfoDTO employeeInfo = ThirdPartyPushSysData.EmployeeInfoDTO.builder()
                    .userId(record.getUserId())
                    .build();
            builder.employeeInfo(employeeInfo);

            // 根据配置决定是否包含客户信息
            ThirdPartyPushSysData.CustomerInfoDTO customerInfo = buildCustomerInfo(customerId, formAdvanceSetting.getCustomerPropertyIdJson(), formId);
            builder.customerInfo(customerInfo);

            return builder.build();

        } catch (Exception e) {
            log.error("[表单提交推送处理] 构建推送数据异常 customerId={}, formId={}, error={}",
                    customerId, formId, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 构建客户信息
     *
     * @param customerId               客户ID
     * @param customerPropertyIdJson   客户属性ID配置JSON
     * @return 客户信息DTO
     */
    public ThirdPartyPushSysData.CustomerInfoDTO buildCustomerInfo(CustomerId customerId, String customerPropertyIdJson, Long formId) {
        WeCustomerVO customerByUserId = customerService.getCustomerByUserId(customerId.getExternalUserid(), customerId.getUserId(), customerId.getCorpId());
        List<ThirdPartyPushSysData.CustomerExtendPropertyInfo> customerFields = weCustomerExtendPropertyService.selectCustomerExternalPropertyMappingById(JSON.parseArray(customerPropertyIdJson, Integer.class), customerId, formId);
        return ThirdPartyPushSysData.CustomerInfoDTO.builder()
                .corpId(customerId.getCorpId())
                .userId(customerId.getUserId())
                .externalUserid(customerId.getExternalUserid())
                .name(customerByUserId == null? "" :customerByUserId.getName())
                .phone(customerByUserId == null? "" :customerByUserId.getRemarkMobiles())
                .birthday(customerByUserId == null? null :customerByUserId.getBirthday())
                .email(customerByUserId == null? "" :customerByUserId.getEmail())
                .address(customerByUserId == null? "" :customerByUserId.getAddress())
                .description(customerByUserId == null? "" :customerByUserId.getDescription())
                .extendFields(customerFields)
                .build();
    }



    /**
     * 判断推送是否启用
     *
     * @param pushContentFlag 推送内容开关
     * @return 是否启用
     */
    private boolean isPushEnabled(Boolean pushContentFlag) {
        return pushContentFlag != null && pushContentFlag;
    }
}
