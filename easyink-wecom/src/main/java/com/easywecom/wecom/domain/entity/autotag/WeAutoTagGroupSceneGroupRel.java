package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 群标签场景与群关系表(WeAutoTagGroupSceneGroupRel)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:34
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagGroupSceneGroupRel {
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 主键id
     */
    private Long groupSceneId;
    /**
     * 群id
     */
    private String groupId;

    public WeAutoTagGroupSceneGroupRel(String groupId) {
        this.groupId = groupId;
    }
}

