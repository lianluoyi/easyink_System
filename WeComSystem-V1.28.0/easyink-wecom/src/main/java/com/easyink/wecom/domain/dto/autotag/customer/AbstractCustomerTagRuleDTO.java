package com.easyink.wecom.domain.dto.autotag.customer;

import com.easyink.wecom.domain.dto.autotag.TagRuleWithUserDTO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerRuleEffectTime;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerScene;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerSceneTagRel;

import java.util.List;

/**
 * 抽象的新客标签类型标签规则DTO
 *
 * @author tigger
 * 2022/2/28 13:37
 **/
public abstract class AbstractCustomerTagRuleDTO extends TagRuleWithUserDTO {
    /**
     * 公共参数的新客标签类型规则生效时间entity转换方法
     *
     * @return
     */
    public abstract WeAutoTagCustomerRuleEffectTime convertToWeAutoTagCustomerRuleEffectTime();

    /**
     * 公共参数的新客标签类型规则场景entity转换方法
     *
     * @return
     */
    public abstract List<WeAutoTagCustomerScene> convertToWeAutoTagCustomerSceneList();

    /**
     * 公共参数的新客标签类型规则场景与标签关系entity转换方法
     *
     * @param tagIdList
     * @return
     */
    public abstract List<WeAutoTagCustomerSceneTagRel> convertToWeAutoTagCustomerSceneTagRelList(List<String> tagIdList);
}
