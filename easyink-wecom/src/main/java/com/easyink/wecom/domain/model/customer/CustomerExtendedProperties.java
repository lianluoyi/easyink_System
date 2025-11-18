package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户扩展属性
 * @author tigger
 * 2025/1/14 17:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerExtendedProperties {

    /**
     * 客户扩展属性id
     */
    private Long id;

    /**
     * 客户扩展属性类型
     */
    private Integer type;
    /**
     * 字段名称
     */
    private String name;

    /**
     * 客户扩展属性值
     */
    private String value;
}
