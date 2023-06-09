package com.easyink.wecom.domain.dto.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.form.WeFormGroup;
import com.easyink.wecom.login.util.LoginTokenService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 修改表单分组DTO
 *
 * @author tigger
 * 2023/1/9 16:40
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormGroupUpdateDTO {

    /**
     * 分组id
     */
    @NotNull(message = "分组id不能为空")
    private Integer id;
    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    private String name;

    /**
     * 校验基础字段
     *
     * @param corpId 企业id
     */
    public void valid(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        if (StringUtils.isBlank(this.getName())) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_NAME_IS_NOT_BLANK);
        }
    }

    /**
     * 转化为修改的entity
     *
     * @return WeFormGroup
     */
    public WeFormGroup toEntity() {
        WeFormGroup group = new WeFormGroup();
        group.setId(this.getId());
        group.setName(this.getName());
        group.setUpdateBy(LoginTokenService.getLoginUser());
        return group;
    }
}
