package com.easyink.wecom.domain.vo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单简单视图VO
 *
 * @author tigger
 * 2023/1/13 10:42
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSimpleInfoVO {

    /**
     * 表单id
     */
    private Integer formId;
    /**
     * 表单头图
     */
    private String headImageUrl;
    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单说明
     */
    private String description;


}
