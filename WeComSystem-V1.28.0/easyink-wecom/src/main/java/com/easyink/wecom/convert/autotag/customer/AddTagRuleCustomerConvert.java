package com.easyink.wecom.convert.autotag.customer;

import com.easyink.wecom.convert.autotag.AddTagRuleConvert;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerRuleEffectTime;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerScene;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerSceneTagRel;

import java.util.List;

/**
 * @author tigger
 * 2022/2/28 11:44
 **/
public interface AddTagRuleCustomerConvert extends AddTagRuleConvert {


    /**
     * 转换为新政对应新客标签类型规则生效时间entity
     *
     * @param ruleId 标签规则id
     * @return
     */
    WeAutoTagCustomerRuleEffectTime toWeAutoTagCustomerRuleEffectTime(Long ruleId);

    /**
     * 转换为新增对应新客标签类型规则场景entity
     *
     * @param ruleId 标签规则id
     * @return
     */
    List<WeAutoTagCustomerScene> toWeAutoTagCustomerSceneList(Long ruleId, String corpId);


    /**
     * 转换为新增对应新客标签类型规则场景与标签关系entity
     *
     * @param groupSceneIdList 群场景id列表
     * @return
     */
    List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList(List<Long> groupSceneIdList, Long ruleId);

}
