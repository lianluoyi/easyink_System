package com.easywecom.wecom.convert.autotag;

import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRule;

/**
 * 修改标签规则转换接口
 *
 * @author tigger
 * 2022/2/27 17:27
 **/
public interface UpdateTagRuleConvert {
    /**
     * 转换为修改对应标签规则entity
     *
     * @return 修改的标签规则entity
     */
    WeAutoTagRule toWeAutoTagRule();

}
