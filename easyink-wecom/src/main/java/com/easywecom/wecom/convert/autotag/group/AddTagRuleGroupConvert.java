package com.easywecom.wecom.convert.autotag.group;

import com.easywecom.wecom.convert.autotag.AddTagRuleConvert;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupScene;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;

import java.util.List;

/**
 * 新增群标签类型转换接口
 *
 * @author tigger
 * 2022/2/28 9:40
 **/
public interface AddTagRuleGroupConvert extends AddTagRuleConvert {
    /**
     * 转换为新增对应的群场景entity
     *
     * @param ruleId 规则id
     * @return
     */
    List<WeAutoTagGroupScene> toWeAutoTagGroupSceneList(Long ruleId, String corpId);

    /**
     * 转换为新增对应的群场景与群关系entity
     *
     * @param groupSceneIdList 群场景id列表
     * @return
     */
    List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList(List<Long> groupSceneIdList,Long ruleId);

    /**
     * 转换为新增对应的群场景与标签关系entity
     *
     * @param groupSceneIdList 群场景id列表
     * @return
     */
    List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList(List<Long> groupSceneIdList, Long ruleId);

}
