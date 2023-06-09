package com.easyink.wecom.domain.vo.form;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表单所有分组树
 *
 * @author wx
 * 2023/3/7 15:15
 **/
@Data
@NoArgsConstructor
public class FormGroupTrees {
    /**
     * 企业表单分组
     */
    List<FormGroupTreeVO> corpFormGroup;

    /**
     * 部门表单分组
     */
    List<FormGroupTreeVO> departmentFormGroup;

    /**
     * 个人表单分组
     */
    List<FormGroupTreeVO> selfFormGroup;
}
