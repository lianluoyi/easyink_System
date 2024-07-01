package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * @author tigger
 * 2024/2/7 14:40
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdAndExternalUserIdModel {

    /**
     * 员工id
     */
    private String userId;

    /**
     * 客户id
     */
    private String externalUserid;

}
