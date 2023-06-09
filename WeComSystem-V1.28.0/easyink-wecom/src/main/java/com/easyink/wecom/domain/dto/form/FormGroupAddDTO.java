package com.easyink.wecom.domain.dto.form;

import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.entity.form.WeFormGroup;
import com.easyink.wecom.domain.enums.form.FormSourceType;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeDepartmentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

/**
 * 添加表单dto
 *
 * @author tigger
 * 2023/1/9 15:43
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormGroupAddDTO {
    /**
     * 父分组id(不传默认顶层分组)
     */
    private Integer parentId;

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    private String name;

    /**
     * 分组所属类别(1:企业 2: 部门 3:个人) {@link FormSourceType}
     */
    @Max(3)
    @Min(1)
    private Integer sourceType;
    /**
     * 所属部门id
     */
    private Long departmentId;

    /**
     * 校验字段
     *
     * @param corpId 企业id
     */
    public void valid(String corpId) {

        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        Optional<FormSourceType> sourceType = FormSourceType.getByCode(this.getSourceType());
        if (!sourceType.isPresent()) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_SOURCE_TYPE_ERROR);
        }

        if (StringUtils.isBlank(this.getName())) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_NAME_IS_NOT_BLANK);
        }

    }

    /**
     * 转化为entity
     *
     * @param corpId 企业id
     * @return WeFormGroup
     */
    public WeFormGroup toEntity(String corpId) {
        WeFormGroup entity
                = new WeFormGroup();

        LoginUser loginUser = LoginTokenService.getLoginUser();

        entity.setCorpId(corpId);
        entity.setPId(this.getParentId());
        entity.setName(this.getName());
        entity.setSourceType(this.getSourceType());
        // 为部门分组时, 设置为当前登录用户的部门id
        if (FormSourceType.DEPARTMENT.getCode().equals(this.getSourceType())) {
            if(this.departmentId == null){
                // 保存为当前部门下
                entity.setDepartmentId(SpringUtils.getBean(WeDepartmentService.class).selectDepartmentIdByUserId(loginUser.getUserId(), corpId));
            }else{
                // 保存到指定部门下
                entity.setDepartmentId(this.departmentId);
            }
        }
        entity.setCreateBy(loginUser);
        entity.setUpdateBy(loginUser);
        return entity;
    }
}
