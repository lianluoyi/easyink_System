package com.easywecom.wecom.convert.autotag.keyword;


import com.easywecom.wecom.convert.autotag.TagRuleUserRelConvert;
import com.easywecom.wecom.convert.autotag.UpdateTagRuleConvert;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;

import java.util.List;

/**
 * 修改关键词标签类型转换接口
 *
 * @author tigger
 * 2022/2/27 18:27
 **/
public interface UpdateTagRuleKeywordConvert extends UpdateTagRuleConvert, TagRuleUserRelConvert {
    /**
     * 转换为修改对应的关键词entity
     *
     * @return
     */
    List<WeAutoTagKeyword> toWeAutoTagKeywordList();

    /**
     * 转换为修改对应的关键词与标签关系entity
     *
     * @return
     */
    List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList();

}
