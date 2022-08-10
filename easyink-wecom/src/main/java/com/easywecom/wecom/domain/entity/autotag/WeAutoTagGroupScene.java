package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 群标签场景表(WeAutoTagGroupScene)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:33
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagGroupScene {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 规则id
     */
    private Long ruleId;


    public WeAutoTagGroupScene(Long id) {
        this.id = id;
    }
}

