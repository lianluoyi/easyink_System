package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户昵称model
 * @author tigger
 * 2023/12/26 16:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUserNameModel {
    /**
     * 员工id
     */
    private String userId;
    /**
     * 客户昵称
     */
    private String customerName;

}
