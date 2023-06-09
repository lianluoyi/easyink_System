package com.easyink.wecom.convert.autotag.customer;

import com.easyink.wecom.convert.autotag.UpdateTagRuleConvert;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerRuleEffectTime;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerScene;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerSceneTagRel;

import java.util.List;

/**
 * @author tigger
 * 2022/2/28 13:31
 **/
public interface UpdateTagRuleCustomerConvert extends UpdateTagRuleConvert {


    /**
     * 转换为修改对应新客标签类型规则生效时间entity
     *
     * @param ruleId 规则id
     * @return
     */
    WeAutoTagCustomerRuleEffectTime toWeAutoTagCustomerRuleEffectTime(Long ruleId);


    /**
     * 转换为修改对应新客标签类型规则场景entity
     *
     * @param ruleId 规则id
     * @return
     */
    List<WeAutoTagCustomerScene> toWeAutoTagCustomerSceneList(Long ruleId, String corpId);


    /**
     * 转换为修改对应新客标签类型规则场景与标签关系entity
     *
     * @param customerSceneIdList 新客场景id列表
     * @return
     */
    List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList(List<Long> customerSceneIdList, Long ruleId);


}
