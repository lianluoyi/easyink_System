package com.easyink.wecom.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.easyink.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/21 0021 23:46
 */
@Data
public class WeLeaveUserVO extends BaseEntity {
    private String userName;

    private String department;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dimissionTime;

    private Integer allocateCustomerNum;

    private Integer allocateGroupNum;

    private Integer isActivate;

    private String userId;

    private Integer isAllocate;

}
