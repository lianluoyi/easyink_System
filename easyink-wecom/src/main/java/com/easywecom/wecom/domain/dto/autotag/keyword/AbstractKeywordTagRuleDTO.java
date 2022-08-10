package com.easywecom.wecom.domain.dto.autotag.keyword;

import com.easywecom.wecom.domain.dto.autotag.TagRuleWithUserDTO;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;

import java.util.List;

/**
 * 抽象的关键词标签类型标签规则DTO
 *
 * @author tigger
 * 2022/2/28 10:18
 **/
public abstract class AbstractKeywordTagRuleDTO extends TagRuleWithUserDTO {
    /**
     * 公共参数的关键词标签类型规则关键词entity转换方法
     *
     * @return
     */
    public abstract List<WeAutoTagKeyword> convertToWeAutoTagKeywordList();

    /**
     * 公共参数的新客标签类型规则标签entity转换方法
     *
     * @return
     */
    public abstract List<WeAutoTagKeywordTagRel> convertToWeAutoTagKeywordTagRelList();
}
