package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户UnionId model
 * @author tigger
 * 2024/11/4 16:04
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUnionIdModel {
    /**
     * 客户externalUserId
     */
    private String externalUserid;

    /**
     * 客户unionId
     */
    private String unionId;
}
