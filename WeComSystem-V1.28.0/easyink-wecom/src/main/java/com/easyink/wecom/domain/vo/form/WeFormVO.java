package com.easyink.wecom.domain.vo.form;

import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.entity.form.WeForm;
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
public class WeFormVO extends WeForm {
    /**
     * 启用标识(0: 未启用 1:启用)
     */
    @JsonIgnore
    private Boolean enableFlag;
    /**
     * 删除标识 0: 未删除 1:删除
     */
    @JsonIgnore
    private Integer delFlag;
    /**
     * 唯一键删除id(删除的时候给deleteId设置为主键id(不重复))
     */
    @JsonIgnore
    private Integer deleteId;

    public WeFormVO(WeForm form) {
        BeanUtils.copyBeanProp(this, form);
    }
}
