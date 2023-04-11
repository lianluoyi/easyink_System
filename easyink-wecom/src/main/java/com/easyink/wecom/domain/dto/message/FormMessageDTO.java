package com.easyink.wecom.domain.dto.message;

import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormMaterial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单链接DTO
 *
 * @author wx
 * 2023/3/6 15:48
 **/
@Data
@NoArgsConstructor
public class FormMessageDTO {

    @ApiModelProperty("表单id")
    private Long formId;

    @ApiModelProperty("表单标题")
    private String linkTitle;

    @ApiModelProperty("表单")
    private WeFormMaterial form;

    public FormMessageDTO(Long formId) {
        this.formId = formId;
    }
}
