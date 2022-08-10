package com.easywecom.wecom.convert.autotag.keyword;


import com.easywecom.wecom.convert.autotag.AddTagRuleConvert;
import com.easywecom.wecom.convert.autotag.TagRuleUserRelConvert;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;

import java.util.List;

/**
 * 新增关键词标签类型转换接口
 *
 * @author tigger
 * 2022/2/27 18:27
 **/
public interface AddTagRuleKeywordConvert extends AddTagRuleConvert, TagRuleUserRelConvert {
    /**
     * 转换为新增对应的关键词entity
     *
     * @param ruleId 标签规则id
     * @return
     */
    List<WeAutoTagKeyword> toWeAutoTagKeywordList(Long ruleId);

    /**
     * 转换为新增对应的关键词与标签关系entity
     *
     * @param ruleId 标签规则id
     * @return
     */
    List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList(Long ruleId);

}
