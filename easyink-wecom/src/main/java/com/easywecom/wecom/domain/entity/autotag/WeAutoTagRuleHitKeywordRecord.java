package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 客户打标签记录表(WeAutoTagRuleHitKeywordRecord)表实体类
 *
 * @author tigger
 * @since 2022-03-02 14:51:19
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagRuleHitKeywordRecord {
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 员工id
     */
    private String userId;
    /**
     * 触发的关键词
     */
    private String keyword;
    /**
     * 触发文本
     */
    private String fromText;
    /**
     * 命中时间
     */
    private Date hitTime;



}

