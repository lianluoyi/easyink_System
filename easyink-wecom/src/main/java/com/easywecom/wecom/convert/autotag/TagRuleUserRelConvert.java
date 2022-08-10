package com.easywecom.wecom.convert.autotag;

import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;

import java.util.List;

/**
 * 通用标签规则与员工使用范围转换接口
 *
 * @author tigger
 * 2022/2/28 9:50
 **/
public interface TagRuleUserRelConvert {
    /**
     * 转换为员工使用范围entity
     *
     * @param ruleId 标签规则id
     * @return
     */
    List<WeAutoTagUserRel> toWeAutoTagUserRel(Long ruleId);
}
