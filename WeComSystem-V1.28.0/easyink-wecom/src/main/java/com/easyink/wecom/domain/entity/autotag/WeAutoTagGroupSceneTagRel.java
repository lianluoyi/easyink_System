package com.easyink.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 群标签场景与标签关系表(WeAutoTagGroupSceneTagRel)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:35
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagGroupSceneTagRel {
    /**
     * 所属规则id
     */
    private Long ruleId;
    /**
     * 群场景id
     */
    private Long groupSceneId;
    /**
     * 标签id
     */
    private String tagId;

    public WeAutoTagGroupSceneTagRel(String tagId) {
        this.tagId = tagId;
    }
}

