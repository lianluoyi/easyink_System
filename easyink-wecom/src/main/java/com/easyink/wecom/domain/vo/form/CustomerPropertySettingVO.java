package com.easyink.wecom.domain.vo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户自定义属性vo
 *
 * @author tigger
 * 2025/8/27 16:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPropertySettingVO {
    /**
     * 属性id
     */
    private String id;

    /**
     * 属性名称
     */
    private String name;

}
