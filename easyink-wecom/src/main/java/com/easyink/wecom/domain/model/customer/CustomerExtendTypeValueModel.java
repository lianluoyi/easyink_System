package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tigger
 * 2025/8/5 19:04
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerExtendTypeValueModel {
    /**
     * 类型
     */
    private Integer propType;

    /**
     * 值
     */
    private String propValue;
}
