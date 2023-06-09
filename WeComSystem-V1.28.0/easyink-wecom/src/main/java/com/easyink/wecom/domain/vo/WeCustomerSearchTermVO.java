package com.easyink.wecom.domain.vo;

import com.easyink.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/21 0021 23:45
 */
@Data
public class WeCustomerSearchTermVO extends BaseEntity {
    private String name;

    private String userId;

    private String tagIds;
}
