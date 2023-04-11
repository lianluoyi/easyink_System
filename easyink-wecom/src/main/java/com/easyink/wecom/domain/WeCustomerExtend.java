package com.easyink.wecom.domain;

import lombok.Data;

/**
 * 获取筛选后所需搜索字段
 */
@Data
public class WeCustomerExtend {

    /**
     * 筛选后获取的用户id
     */
    private String externalUserid;

    /**
     * 筛选后获取的员工id
     */
    private String userId;
}
