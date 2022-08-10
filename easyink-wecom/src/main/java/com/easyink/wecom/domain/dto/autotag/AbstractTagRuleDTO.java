package com.easyink.wecom.domain.dto.autotag;

import com.easyink.wecom.domain.entity.autotag.WeAutoTagRule;

/**
 * 抽象标签规则DTO
 *
 * @author tigger
 * 2022/2/28 10:22
 **/
public abstract class AbstractTagRuleDTO {
    /**
     * 公共参数的标签规则entity转换方法
     *
     * @return
     */
    public abstract WeAutoTagRule convertToWeAutoTagRule();
}
