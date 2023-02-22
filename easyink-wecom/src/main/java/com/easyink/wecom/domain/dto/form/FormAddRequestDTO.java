package com.easyink.wecom.domain.dto.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 添加表单请求DTO
 *
 * @author tigger
 * 2023/1/10 9:37
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormAddRequestDTO {

    /**
     * 表单
     */
    @Valid
    @NotNull(message = "表单基础信息不能为空")
    private FormAddDTO form;
    /**
     * 表单设置
     */
    @Valid
    @NotNull(message = "表单设置不能为空")
    private FormSettingAddDTO formSetting;

    /**
     * 校验
     */
    public void valid() {
        if (this.form == null || this.formSetting == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        // 表单校验
        this.form.valid();

        // 表单设置校验
        this.formSetting.valid();


    }


}
