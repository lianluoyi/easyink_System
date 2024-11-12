package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工客户idmodel
 *
 * @author tigger
 * 2024/2/7 14:40
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdAndExternalUserIdModel {

    /**
     */
    private String userId;
    /**
     * 客户id
     */
    private String externalUserid;

}
