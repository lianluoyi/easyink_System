package com.easyink.wecom.domain.vo.form;

import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 表单VO
 *
 * @author tigger
 * 2023/1/15 9:32
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeFormSettingVO extends WeFormAdvanceSetting {
    /**
     * 关联表单id
     */
    @JsonIgnore
    private Integer formId;
    /**
     * 标签设置
     */
    private CustomerLabelSettingDetailVO labelSetting;

    public WeFormSettingVO(WeFormAdvanceSetting formSetting, CustomerLabelSettingDetailVO labelSetting) {
        BeanUtils.copyBeanProp(this, formSetting);
        this.labelSetting = labelSetting;
    }
}
