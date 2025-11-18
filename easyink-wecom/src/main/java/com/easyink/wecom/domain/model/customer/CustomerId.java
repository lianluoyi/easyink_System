package com.easyink.wecom.domain.model.customer;

import com.easyink.common.utils.StringUtils;
import lombok.Data;
import lombok.Value;

/**
 * 客户id 值对象
 * @author tigger
 * 2025/1/14 11:05
 **/
@Data
@Value(staticConstructor = "valueOf")
public class CustomerId {
    /**
     * 员工id
     */
    String userId;
    /**
     * 客户id
     */
    String externalUserid;

    /**
     * 企业id
     */
    String corpId;

    /**
     * 是否非法
     * @return true: 非法 flase: 合法
     */
    public boolean invalid() {
        return StringUtils.isAnyBlank(this.userId, this.externalUserid, this.corpId);

    }
}
