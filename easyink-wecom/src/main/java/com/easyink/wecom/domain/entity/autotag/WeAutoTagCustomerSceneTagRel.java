package com.easyink.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 新客标签场景与标签关系表(WeAutoTagCustomerSceneTagRel)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:32
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagCustomerSceneTagRel {
    /**
     * 所属规则id
     */
    private Long ruleId;
    /**
     * 客户场景id
     */
    private Long customerSceneId;
    /**
     * 标签id
     */
    private String tagId;

    public WeAutoTagCustomerSceneTagRel(String tagId) {
        this.tagId = tagId;
    }
}

