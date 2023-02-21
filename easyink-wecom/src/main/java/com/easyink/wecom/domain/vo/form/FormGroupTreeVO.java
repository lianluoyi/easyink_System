package com.easyink.wecom.domain.vo.form;

import com.easyink.common.core.domain.Tree;
import com.easyink.wecom.domain.entity.form.WeFormGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 表单分组VO
 *
 * @author tigger
 * 2023/1/9 15:22
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FormGroupTreeVO extends Tree<WeFormGroup> {
    /**
     * 排序号
     */
    private Integer sort;

}
