package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeFormSendRecord;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.enums.form.SendSourceTypeEnum;
import com.easyink.wecom.domain.enums.form.push.FormRecordStatusEnum;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.mapper.WeFormSendRecordMapper;
import com.easyink.wecom.service.WeFormSendRecordService;
import com.easyink.wecom.service.form.WeFormAdvanceSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 满意度表单发送记录Service业务层处理
 *
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeFormSendRecordServiceImpl extends ServiceImpl<WeFormSendRecordMapper, WeFormSendRecord> implements WeFormSendRecordService {

    private final WeFormAdvanceSettingService weFormAdvanceSettingService;

    @Override
    public Long saveFormRecord(CustomerId customerId, Long formId) {
        return saveFormRecord(customerId, formId, null);
    }

    @Override
    public Long saveFormRecord(CustomerId customerId, Long formId, String state) {
        if (customerId == null || customerId.invalid() || formId == null) {
            log.warn("[保存发送表单记录] 参数缺失");
            return null;
        }

        WeFormSendRecord record = new WeFormSendRecord();
        record.setCorpId(customerId.getCorpId());
        record.setExternalUserid(customerId.getExternalUserid());
        record.setUserId(customerId.getUserId());
        record.setFormId(formId);
        record.setSource(SendSourceTypeEnum.WELCOME_MSG.getCode());
        record.setSentTime(new Date());
        record.setState(StringUtils.isNotBlank(state) ? state : org.apache.commons.lang3.StringUtils.EMPTY); // 设置state字段，兼容其他渠道设置为空字符串
        boolean saved = this.save(record);
        return saved ? record.getId() : null;
    }

    @Override
    public Long recordFormSubmit(CustomerId customerId, Long formId, String formResultData) {
        if (customerId.invalid() || formId == null) {
            log.warn("[表单推送] 记录表单提交失败：参数不完整");
            return null;
        }

        WeFormSendRecord record = getByCustomerAndForm(customerId, formId);
        if (record == null) {
            log.warn("[表单推送] 未找到客户{}的表单{}发送记录", customerId, formId);
            return null;
        }

        record.setSubmitTime(new Date());
        record.setSubmitStatus(FormRecordStatusEnum.SubmitStatusEnum.SUBMITTED.getCode());
        record.setFormResultData(formResultData);
        boolean updateRow = this.updateById(record);
        return updateRow? record.getId(): null;
    }

    @Override
    public List<WeFormSendRecord> getTimeoutRecords(String corpId, Integer timeoutHours) {
        if (StringUtils.isBlank(corpId) || timeoutHours == null || timeoutHours <= 0) {
            return null;
        }

        Date timeoutDate = DateUtils.addHours(new Date(), -timeoutHours);
        return baseMapper.selectTimeoutRecords(corpId, timeoutDate);
    }

    @Override
    public boolean updatePushStatus(Long recordId, Integer pushStatus) {
        if (recordId == null || pushStatus == null) {
            return false;
        }

        WeFormSendRecord record = this.getById(recordId);
        if (record == null) {
            return false;
        }

        record.setPushStatus(pushStatus);
        if (FormRecordStatusEnum.PushStatusEnum.PUSHED_SUCCESS.getCode().equals(pushStatus)) {
            record.setPushTime(new Date());
        }

        return this.updateById(record);
    }

    @Override
    public boolean batchUpdateTimeoutPushStatus(List<Long> recordIds, Integer timeoutPushStatus) {
        if (recordIds == null || recordIds.isEmpty() || timeoutPushStatus == null) {
            return false;
        }

        Date now = new Date();
        return baseMapper.batchUpdateTimeoutPushStatus(recordIds, timeoutPushStatus, now) > 0;
    }

    @Override
    public WeFormSendRecord getByCustomerAndForm(CustomerId customerId, Long formId) {
        if (customerId.invalid() || formId == null) {
            return null;
        }
        return baseMapper.selectByCustomerAndForm(customerId.getCorpId(), customerId.getUserId(), customerId.getExternalUserid(), formId);
    }

    @Override
    public List<Long> batchSaveFormRecord(CustomerId customerId, List<Long> formIds, SendSourceTypeEnum sourceTypeEnum) {
        return batchSaveFormRecord(customerId, formIds, sourceTypeEnum, null);
    }

    @Override
    public List<Long> batchSaveFormRecord(CustomerId customerId, List<Long> formIds, SendSourceTypeEnum sourceTypeEnum, String state) {
        log.info("[批量保存表单发送记录] customerId: {}, formIds: {}, source: {}, state: {}", customerId, formIds, sourceTypeEnum, state);
        if (customerId == null || customerId.invalid() || CollectionUtils.isEmpty(formIds)) {
            log.warn("[批量保存表单发送记录] 参数缺失, customerId: {}, formIds: {}", customerId, formIds);
            return new ArrayList<>();
        }

        // 批量获取表单高级配置
        List<WeFormAdvanceSetting> settingList = weFormAdvanceSettingService.listByIds(formIds);
        Map<Long, WeFormAdvanceSetting> settingMap = settingList.stream()
                .collect(Collectors.toMap(WeFormAdvanceSetting::getFormId, setting -> setting));

        // 筛选出开启推送开关的表单ID
        List<Long> enabledFormIds = new ArrayList<>();
        for (Long formId : formIds) {
            WeFormAdvanceSetting setting = settingMap.get(formId);
            if (setting != null && Boolean.TRUE.equals(setting.getPushContentFlag())) {
                enabledFormIds.add(formId);
            }
        }

        if (CollectionUtils.isEmpty(enabledFormIds)) {
            log.info("[批量保存表单发送记录] 没有开启推送开关的表单, customerId: {}, formIds: {}", customerId, formIds);
            return new ArrayList<>();
        }

        // 为开启推送开关的表单创建发送记录
        List<WeFormSendRecord> records = new ArrayList<>();
        Date sentTime = new Date();
        for (Long formId : enabledFormIds) {
            WeFormSendRecord record = new WeFormSendRecord();
            record.setCorpId(customerId.getCorpId());
            record.setExternalUserid(customerId.getExternalUserid());
            record.setUserId(customerId.getUserId());
            record.setFormId(formId);
            record.setSource(sourceTypeEnum.getCode());
            record.setSentTime(sentTime);
            record.setState(StringUtils.isNotBlank(state) ? state : org.apache.commons.lang3.StringUtils.EMPTY); // 设置state字段，兼容其他渠道设置为空字符串
            records.add(record);
        }

        boolean result = this.saveBatch(records);
        if (!result) {
            log.error("[批量保存表单发送记录] 保存失败, customerId: {}, enabledFormIds: {}", customerId, enabledFormIds);
            return new ArrayList<>();
        }

        List<Long> recordIds = records.stream()
                .map(WeFormSendRecord::getId)
                .collect(Collectors.toList());

        log.info("[批量保存表单发送记录] 保存成功, customerId: {}, 保存记录数: {}", customerId, records.size());
        return recordIds;
    }

    @Override
    public String getStateByFormId(CustomerId customerId, Long formId) {
        if (formId == null) {
            return StringUtils.EMPTY;
        }
        return this.baseMapper.selectStateByFormId(customerId.getCorpId(), customerId.getUserId(), customerId.getExternalUserid(), formId);
    }
}
