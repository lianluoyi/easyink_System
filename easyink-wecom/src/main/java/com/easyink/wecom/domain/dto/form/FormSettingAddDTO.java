package com.easyink.wecom.domain.dto.form;

import com.alibaba.fastjson.JSON;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.enums.form.DeadLineType;
import com.easyink.wecom.domain.enums.form.SubmitActionType;
import com.easyink.wecom.domain.enums.form.SubmitCntType;
import com.easyink.wecom.domain.model.form.CustomerLabelSettingModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;

/**
 * 添加表单DTO
 *
 * @author tigger
 * 2023/1/10 9:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSettingAddDTO {

    /**
     * 截止时间类型(1: 永久有效 2:自定义日期)
     */
    @Range(min = 1, max = 2, message = "截止时间类型异常")
    private Integer deadLineType;
    /**
     * 截止时间的自定义时间(deadLineType == 1时才有)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date customDate;
    /**
     * 公众号设置
     */
    private String weChatPublicPlatform;
    /**
     * 提交次数类型(1: 不限 2:每个客户限提交1次)
     */
    @Range(min = 1, max = 2, message = "提交次数类型异常")
    private Integer submitCntType;
    /**
     * 提交结果行为类型(1:不跳转 2:跳转结果页面 3:跳转连接)
     */
    @Range(min = 1, max = 3, message = "提交结果类型异常")
    private Integer submitActionType;
    /**
     * 提交结果行为详情参数(当 submitActionType不为1的时候有用)
     */
    private ActionInfoParamDTO actionInfoParam;
    /**
     * 行为通知开关(false:关闭 true:开启)
     */
    private Boolean actionNoteFlag;
    /**
     * 轨迹记录开关(false:关闭 true:开启)
     */
    private Boolean tractRecordFlag;
    /**
     * 客户标签开关(false:关闭 true:开启)
     */
    private Boolean customerLabelFlag;
    /**
     * 客户标签开关设置详情json
     */
    private CustomerLabelSettingModel labelSetting;
    /**
     * 推送内容开关
     */
    private Boolean pushContentFlag;

    /**
     * 客户自定义属性id列表
     */
    private List<String> customerPropertyIdList;

    /**
     * 超时配置
     */
    private Integer timeoutHours;

    /**
     * 校验
     */
    public void valid() {
        // 截止时间
        DeadLineType deadLineType = DeadLineType.validCode(this.deadLineType);
        if (DeadLineType.CUSTOM == deadLineType && customDate == null) {
            throw new CustomException(ResultTip.TIP_FORM_CUSTOM_DATE_IS_NOT_NULL);
        }

        // 提交次数
        SubmitCntType.validCode(this.submitCntType);

        // 提交行为
        SubmitActionType submitActionType = SubmitActionType.validCode(this.submitActionType);
        submitActionType.validParam(this.actionInfoParam);

        // 客户标签设置
        if (customerLabelFlag != null && customerLabelFlag) {
            if (labelSetting == null) {
                throw new CustomException(ResultTip.TIP_FORM_CUSTOMER_LABEL_SETTING_IS_NOT_NULL);
            }
        }

    }

    /**
     * 转化为持久化实体
     *
     * @param formId 表单id
     * @return WeFormAdvanceSetting
     */
    public WeFormAdvanceSetting toEntity(Long formId) {
        WeFormAdvanceSetting setting = new WeFormAdvanceSetting();

        setting.setFormId(formId);
        setting.setDeadLineType(this.getDeadLineType());
        setting.setCustomDate(this.getCustomDate());
        setting.setWeChatPublicPlatform(this.getWeChatPublicPlatform());
        setting.setSubmitCntType(this.getSubmitCntType());
        setting.setActionInfoParamJson(JSON.toJSONString(this.getActionInfoParam()));
        setting.setActionNoteFlag(this.getActionNoteFlag());
        setting.setSubmitActionType(this.getSubmitActionType());
        setting.setTractRecordFlag(this.getTractRecordFlag());
        setting.setCustomerLabelFlag(this.getCustomerLabelFlag());
        setting.setLabelSettingJson(JSON.toJSONString(this.getLabelSetting()));
        setting.setPushContentFlag(this.getPushContentFlag());
        setting.setCustomerPropertyIdJson(JSON.toJSONString(this.getCustomerPropertyIdList()));
        setting.setTimeoutHours(this.getTimeoutHours());
        return setting;
    }
}
