package com.easyink.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 关键词与标签关系表(WeAutoTagKeywordTagRel)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:38
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagKeywordTagRel {
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 规则名称
     */
    private String tagId;

    public WeAutoTagKeywordTagRel(String tagId) {
        this.tagId = tagId;
    }
}

