package com.easyink.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 关键词规则表(WeAutoTagKeyword)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:37
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagKeyword {
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 匹配规则 1:模糊匹配 2:精确匹配
     */
    private Integer matchType;
    /**
     * 关键词
     */
    private String keyword;

    public WeAutoTagKeyword(Integer matchType, String keyword) {
        this.matchType = matchType;
        this.keyword = keyword;
    }
}

