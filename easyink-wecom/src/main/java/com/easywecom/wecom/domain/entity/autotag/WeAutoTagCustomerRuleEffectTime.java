package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 新客规则生效时间表(WeAutoTagCustomerRuleEffectTime)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:28
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagCustomerRuleEffectTime {
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 生效开始时间
     */
    private String effectBeginTime;
    /**
     * 生效结束时间
     */
    private String effectEndTime;

}

