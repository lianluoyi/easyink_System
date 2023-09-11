package com.easyink.wecom.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.dto.form.FormCommitDTO;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.entity.form.WeFormOperRecord;
import com.easyink.wecom.domain.vo.form.FormCustomerOperRecordVO;
import com.easyink.wecom.domain.vo.form.FormOperRecordDetailVO;
import com.easyink.wecom.domain.vo.form.FormUserSendRecordVO;

import java.util.Date;
import java.util.List;

/**
 * 智能表单操作记录表(WeFormOperRecord)表服务接口
 *
 * @author wx
 * @since 2023-01-13 11:49:47
 */
public interface WeFormOperRecordService extends IService<WeFormOperRecord> {

    /**
     * 异步增加点击记录
     *
     * @param recordId             表单操作记录id
     * @param formId               表单id
     * @param userId               员工id
     * @param openId               微信公众openId
     * @param weForm               {@link WeForm}
     * @param weFormAdvanceSetting {@link WeFormAdvanceSetting}
     * @param channelType          {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     */
    void syncAddClickRecord(Long recordId, Long formId, String userId, String openId, WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, Integer channelType);


    /**
     * 增加提交记录
     *
     * @param formCommitDTO        {@link FormCommitDTO}
     * @param weForm               {@link WeForm}
     * @param weFormAdvanceSetting {@link WeFormAdvanceSetting}
     * @param weFormOperRecord     {@link WeFormOperRecord}
     */
    void addCommitRecord(FormCommitDTO formCommitDTO, WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, WeFormOperRecord weFormOperRecord);

    /**
     * 为点击/提交表单的客户打标签
     *
     * @param tagIdList         标签Id列表
     * @param corpId            企业id
     * @param userId            员工id
     * @param userName          员工姓名
     * @param externalUserId    外部联系人id
     */
    void setTagForForm(List<String> tagIdList, String corpId, String userId, String userName, String externalUserId);


    /**
     * 获取客户操作记录
     *
     * @param formId        表单id
     * @param timeType      时间类型，1：点击时间，2：提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param customerName  客户名称
     * @param channelType   渠道类型 {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return {@link FormCustomerOperRecordVO}
     */
    List<FormCustomerOperRecordVO> getCustomerOperRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String customerName, Integer channelType);

    /**
     * 获取员工发送记录
     *
     * @param formId        表单id
     * @param timeType      时间类型，1：点击时间，2：提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param userName      员工姓名
     * @return
     */
    List<FormUserSendRecordVO> getUserSendRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String userName);

    /**
     * 获取客户表单详情
     *
     * @param formId            表单id
     * @param channelType       点击渠道
     * @return  {@link FormOperRecordDetailVO}
     */
    List<FormOperRecordDetailVO> getFormResult(Long formId, Integer channelType);

    /**
     * 导出客户操作记录
     *
     * @param formId        表单id
     * @param timeType      时间类型，1：点击时间，2：提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param customerName  客户名称
     * @param channelType   渠道类型 {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    AjaxResult exportCustomerOperRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String customerName, Integer channelType);

    /**
     *
     * @param formId        表单id
     * @param timeType      时间类型，1：点击时间，2：提交时间
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param userName      员工姓名
     * @return
     */
    AjaxResult exportUserSendRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String userName);
}

