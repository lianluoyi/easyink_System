package com.easyink.wecom.domain.dto.autotag;

import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;

import java.util.List;

/**
 * 抽象的带有员工使用范围的标签规则DTO
 *
 * @author tigger
 * 2022/2/28 13:40
 **/
public abstract class AbstractTagRuleWithUserDTO extends TagRuleDTO {
    /**
     * 公共参数的带员工使用范围的标签规则entity转换方法
     *
     * @return
     */
    public abstract List<WeAutoTagUserRel> convertToWeAutoTagUserRelList(Long ruleId);
}
