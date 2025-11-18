package com.easyink.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.wecom.domain.WeFormSendRecord;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushSysData;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.enums.form.push.FormRecordStatusEnum;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.handler.FormSubmitPushHandler;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeCustomerExtendPropertyService;
import com.easyink.wecom.service.WeFormSendRecordService;
import com.easyink.wecom.service.WeThirdPartyPushService;
import com.easyink.wecom.service.form.WeFormAdvanceSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 满意度表单超时检查定时任务
 *
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@RequiredArgsConstructor
@Component("FormSubmitTimeoutTask")
public class FormSubmitTimeoutTask {

    private final WeFormSendRecordService satisfactionFormRecordService;
    private final WeFormAdvanceSettingService formAdvanceSettingService;
    private final WeThirdPartyPushService thirdPartyPushService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerExtendPropertyService weCustomerExtendPropertyService;
    private final FormSubmitPushHandler formSubmitPushHandler;


    /**
     * 每小时执行一次，检查超时未提交的表单
     */
    public void checkTimeoutForms() {
        log.info("[超时表单推送JOB] 开始执行满意度表单超时检查任务");

        try {
            List<String> corpIds = getAllCorpIds();
            for (String corpId : corpIds) {
                processTimeoutFormsForCorp(corpId);
            }
            log.info("[超时表单推送JOB] 表单超时检查任务执行完成");

        } catch (Exception e) {
            log.error("[超时表单推送JOB] 执行满意度表单超时检查任务异常：{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 处理单个企业的超时表单
     *
     * @param corpId 企业ID
     */
    private void processTimeoutFormsForCorp(String corpId) {
        try {
            log.info("[超时表单推送JOB] 开始处理企业{}的超时表单", corpId);

            // 获取所有启用推送且配置了超时时间的表单设置
            List<WeFormAdvanceSetting> enabledSettings = formAdvanceSettingService.list()
                    .stream()
                    .filter(setting -> isPushEnabled(setting.getPushContentFlag()) && isTimeoutConfigured(setting.getTimeoutHours()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(enabledSettings)) {
                log.info("[超时表单推送JOB] 企业{}没有启用的表单推送配置", corpId);
                return;
            }

            // 按超时时间分组处理
            Map<Integer, List<WeFormAdvanceSetting>> settingsByTimeout = enabledSettings.stream()
                    .collect(Collectors.groupingBy(WeFormAdvanceSetting::getTimeoutHours));

            for (Map.Entry<Integer, List<WeFormAdvanceSetting>> entry : settingsByTimeout.entrySet()) {
                Integer timeoutHours = entry.getKey();
                List<WeFormAdvanceSetting> settings = entry.getValue();
                // 处理某个超时时间下的 超时发送表单
                processTimeoutFormsWithTimeout(corpId, timeoutHours, settings);
            }

        } catch (Exception e) {
            log.error("[超时表单推送JOB] 处理企业{}的超时表单异常：{}", corpId, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 处理指定超时时间的表单
     *
     * @param corpId       企业ID
     * @param timeoutHours 超时小时数
     * @param settings     表单设置列表
     */
    private void processTimeoutFormsWithTimeout(String corpId, Integer timeoutHours, List<WeFormAdvanceSetting> settings) {
        try {
            // 获取超时未提交的发送表单记录
            // 1)未提交表单, 等到超时的记录
            // 2)提交表单, 但是推送失败, 需要超时兜底的表单记录
            List<WeFormSendRecord> timeoutRecords = satisfactionFormRecordService.getTimeoutRecords(corpId, timeoutHours);
            if (CollectionUtils.isEmpty(timeoutRecords)) {
                log.info("[超时表单推送JOB] 企业{}在{}小时超时范围内没有未提交的表单", corpId, timeoutHours);
                return;
            }

            // 过滤出存在表单推送配置的表单记录
            List<Long> settingFormIds = settings.stream()
                    .map(WeFormAdvanceSetting::getFormId)
                    .collect(Collectors.toList());

            List<WeFormSendRecord> filteredRecords = timeoutRecords.stream()
                    // 过滤出对应的表单
                    .filter(record -> settingFormIds.contains(record.getFormId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filteredRecords)) {
                log.info("[超时表单推送JOB] 企业{}在{}小时超时范围内没有需要处理的表单", corpId, timeoutHours);
                return;
            }

            log.info("[超时表单推送JOB] 企业{}发现{}个超时未提交的表单，开始处理", corpId, filteredRecords.size());

            // 处理超时记录
            List<Long> processedRecordIds = new ArrayList<>();
            for (WeFormSendRecord record : filteredRecords) {
                // 查找对应的设置
                WeFormAdvanceSetting setting = settings.stream()
                        .filter(s -> s.getFormId().equals(record.getFormId()))
                        .findFirst()
                        .orElse(null);
                boolean pushResult = processTimeoutRecord(record, setting);
                processedRecordIds.add(record.getId());
            }

            // 批量更新超时推送状态
            satisfactionFormRecordService.batchUpdateTimeoutPushStatus(processedRecordIds, FormRecordStatusEnum.TimeoutPushStatusEnum.PUSHED_FINALLY.getCode());
            log.info("[超时表单推送JOB] 企业{}成功处理{}个超时表单", corpId, processedRecordIds.size());

        } catch (Exception e) {
            log.error("[超时表单推送JOB] 处理企业{}超时{}小时表单异常：{}", corpId, timeoutHours, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 处理单个超时记录
     *
     * @param record   超时记录
     * @param settings 设置列表
     * @return 处理结果
     */
    private boolean processTimeoutRecord(WeFormSendRecord record, WeFormAdvanceSetting setting) {
        try {
            if (setting == null) {
                log.warn("[超时表单推送JOB] 未找到表单{}的推送设置", record.getFormId());
                return false;
            }

            // 构建超时推送数据
            ThirdPartyPushSysData pushData = buildTimeoutPushData(record, setting);
            if (pushData == null) {
                log.warn("[超时表单推送JOB] 构建超时推送数据失败：recordId={}", record.getId());
                return false;
            }

            // 推送超时数据
            return thirdPartyPushService.pushFormSubmitData(pushData, record.getId());

        } catch (Exception e) {
            log.error("[超时表单推送JOB] 处理超时记录异常：recordId={}, e={}", record.getId(), ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 构建超时推送数据
     *
     * @param record  表单记录
     * @param setting 表单设置
     * @return 推送数据DTO
     */
    private ThirdPartyPushSysData buildTimeoutPushData(WeFormSendRecord record, WeFormAdvanceSetting setting) {
        try {
            ThirdPartyPushSysData.ThirdPartyPushSysDataBuilder builder = ThirdPartyPushSysData.builder()
                    .corpId(record.getCorpId())
                    .pushType(FormRecordStatusEnum.PushSceneEnum.TIMEOUT_SUBMIT.getCode())
                    .pushTime(new Date());


            // 构建表单信息
            ThirdPartyPushSysData.FormInfoDTO formInfo = ThirdPartyPushSysData.FormInfoDTO.builder()
                    .formId(record.getFormId())
                    .sentTime(record.getSentTime())
                    .submitTime(record.getSubmitTime())
                    .formSubmitResult(record.getFormResultData())
                    .build();
            builder.formInfo(formInfo);

            // 根据配置决定是否包含客户信息数据

            ThirdPartyPushSysData.CustomerInfoDTO customerData = formSubmitPushHandler.buildCustomerInfo(CustomerId.valueOf(record.getUserId(), record.getExternalUserid(), record.getCorpId()), setting.getCustomerPropertyIdJson(), null);
            builder.customerInfo(customerData);


            // 构建员工信息
            ThirdPartyPushSysData.EmployeeInfoDTO employeeInfo = ThirdPartyPushSysData.EmployeeInfoDTO.builder()
                    .userId(record.getUserId())
                    .build();
            builder.employeeInfo(employeeInfo);

            return builder.build();

        } catch (Exception e) {
            log.error("构建超时推送数据异常：recordId={}, error={}", record.getId(), e.getMessage(), e);
            return null;
        }
    }


    /**
     * 获取所有企业ID
     *
     * @return 企业ID列表
     */
    private List<String> getAllCorpIds() {
        List<WeCorpAccount> weCorpAccounts = weCorpAccountService.list(new LambdaQueryWrapper<WeCorpAccount>().select(WeCorpAccount::getCorpId));
        return weCorpAccounts.stream().map(WeCorpAccount::getCorpId).collect(Collectors.toList());
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

    /**
     * 判断是否配置了超时时间
     *
     * @param timeoutHours 超时小时数
     * @return 是否配置
     */
    private boolean isTimeoutConfigured(Integer timeoutHours) {
        return timeoutHours != null && timeoutHours > 0;
    }

    /**
     * 判断是否已超时推送
     *
     * @param timeoutPushStatus 超时推送状态
     * @return 是否已推送
     */
    private boolean isTimeoutPushed(Integer timeoutPushStatus) {
        return timeoutPushStatus != null && timeoutPushStatus == 1;
    }
}
