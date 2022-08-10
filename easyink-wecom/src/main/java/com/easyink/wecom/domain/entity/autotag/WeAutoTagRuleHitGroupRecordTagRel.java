package com.easyink.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户标签命中记录(WeAutoTagRuleHitGroupRecordTagRel)表实体类
 *
 * @author tigger
 * @since 2022-03-02 15:42:28
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagRuleHitGroupRecordTagRel {
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 标签id,去重用
     */
    private String tagId;
    /**
     * 标签名
     */
    private String tagName;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 群id
     */
    private String groupId;
}

