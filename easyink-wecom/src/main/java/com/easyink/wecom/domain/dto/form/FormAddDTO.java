package com.easyink.wecom.domain.dto.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.form.WeForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加表单DTO
 *
 * @author tigger
 * 2023/1/10 9:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormAddDTO {

    /**
     * 表单头图
     */
    private String headImageUrl;
    /**
     * 表单名称
     */
    @NotBlank(message = "表单名称不能为空")
    @Length(min = 1, max = 20, message = "最大不超过20个字符")
    private String formName;
    /**
     * 表单说明
     */
    private String description;
    /**
     * 提交按钮文本描述
     */
    @NotBlank(message = "提交文本不能为空")
    @Length(min = 1, max = 6, message = "最大不超过6个字符")
    private String submitText;

    /**
     * 提交按钮颜色
     */
    @NotBlank(message = "提交按钮颜色不能为空")
    private String submitColor;
    /**
     * 表单字段列表json
     */
    @NotBlank(message = "表单列表不能为空")
    private String formFieldListJson;
    /**
     * 表单分组id
     */
    @NotNull(message = "所属表单分组不能为空")
    private Integer groupId;

    /**
     * 头图开关(false:关闭 true:开启)
     */
    private Boolean headImageOpenFlag;
    /**
     * 显示排序开关(false:关闭 true:开启)
     */
    private Boolean showSortFlag;
    /**
     * 表单说明开关(false:关闭 true:开启)
     */
    private Boolean descriptionFlag;


    /**
     * 校验表单基础项
     */
    public void valid() {
        if (this.headImageOpenFlag != null && this.headImageOpenFlag && StringUtils.isBlank(this.headImageUrl)) {
            throw new CustomException(ResultTip.TIP_FORM_HEAD_IMAGE_IS_NOT_BLANK);
        }
        if (this.descriptionFlag != null && this.descriptionFlag && StringUtils.isBlank(this.description)) {
            throw new CustomException(ResultTip.TIP_FORM_DESC_IS_NOT_BLANK);
        }

    }

    /**
     * 转化为entity
     *
     * @param corpId 企业id
     * @return WeForm
     */
    public WeForm toEntity(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }

        WeForm weForm = new WeForm();
        weForm.setCorpId(corpId);
        weForm.setHeadImageUrl(this.getHeadImageUrl());
        weForm.setFormName(this.getFormName());
        weForm.setDescription(this.getDescription());
        weForm.setSubmitText(this.getSubmitText());
        weForm.setSubmitColor(this.getSubmitColor());
        weForm.setFormFieldListJson(this.getFormFieldListJson());
        weForm.setHeadImageOpenFlag(this.getHeadImageOpenFlag());
        weForm.setShowSortFlag(this.getShowSortFlag());
        weForm.setDescriptionFlag(this.getDescriptionFlag());
        weForm.setGroupId(this.getGroupId());

        return weForm;
    }
}
