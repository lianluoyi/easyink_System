package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户打标签记录表(WeAutoTagRuleHitCustomerRecord)表实体类
 *
 * @author tigger
 * @since 2022-03-02 16:04:52
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagRuleHitCustomerRecord {

    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 员工id
     */
    private String userId;
    /**
     * 添加时间
     */
    private Date addTime;


}

