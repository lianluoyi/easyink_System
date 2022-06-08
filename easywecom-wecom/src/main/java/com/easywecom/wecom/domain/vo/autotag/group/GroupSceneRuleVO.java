package com.easywecom.wecom.domain.vo.autotag.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群场景与规则VO
 *
 * @author tigger
 * 2022/4/12 16:41
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSceneRuleVO {

    /**规则id*/
    private Long ruleId;
    /**群场景id*/
    private Long groupSceneId;

}
