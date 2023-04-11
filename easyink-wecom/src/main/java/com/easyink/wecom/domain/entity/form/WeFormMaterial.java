package com.easyink.wecom.domain.entity.form;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 素材库表单
 *
 * @author wx
 * 2023/3/7 11:22
 **/
@Data
@NoArgsConstructor
public class WeFormMaterial {
    /**
     * 表单id
     */
    private Long formId;
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
