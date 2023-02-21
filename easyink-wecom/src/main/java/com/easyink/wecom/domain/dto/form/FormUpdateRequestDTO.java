package com.easyink.wecom.domain.dto.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 编辑表单请求DTO
 *
 * @author tigger
 * 2023/1/10 9:37
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormUpdateRequestDTO extends FormAddRequestDTO {

    /**
     * 表单id
     */
    @NotNull(message = "请选择需要修改的表单")
    private Integer id;

    @Override
    public void valid() {
        super.valid();
        if (this.id == null) {
            throw new CustomException(ResultTip.TIP_FORM_ID_IS_NOT_NULL);
        }
    }

}
