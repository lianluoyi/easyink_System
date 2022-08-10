package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户打标签记录表(WeAutoTagRuleHitGroupRecord)表实体类
 *
 * @author tigger
 * @since 2022-03-02 15:42:26
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagRuleHitGroupRecord {

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
     * 群id
     */
    private String groupId;
    /**
     * 群名
     */
    private String groupName;
    /**
     * 进群时间
     */
    private Date joinTime;


}

