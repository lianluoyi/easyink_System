package com.easyink.wecom.convert.autotag;

import com.easyink.wecom.domain.entity.autotag.WeAutoTagRule;

/**
 * 新增标签规则转换接口
 *
 * @author tigger
 * 2022/2/27 17:27
 **/
public interface AddTagRuleConvert {
    /**
     * 转换为新增对应的标签规则entity
     *
     * @return 新增的标签规则entity
     */
    WeAutoTagRule toWeAutoTagRule();

}
