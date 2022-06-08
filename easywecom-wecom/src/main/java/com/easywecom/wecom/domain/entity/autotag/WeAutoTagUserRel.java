package com.easywecom.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 标签与员工使用范围表(WeAutoTagUserRel)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:45
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagUserRel {
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 员工id
     */
    private String userId;


}

