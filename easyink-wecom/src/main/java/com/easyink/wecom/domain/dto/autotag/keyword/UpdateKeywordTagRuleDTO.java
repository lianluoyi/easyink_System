package com.easyink.wecom.domain.dto.autotag.keyword;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.convert.autotag.keyword.UpdateTagRuleKeywordConvert;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRule;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 修改关键词标签规则
 *
 * @author tigger
 * 2022/2/27 19:55
 **/
@Data
public class UpdateKeywordTagRuleDTO extends KeywordTagRuleDTO implements UpdateTagRuleKeywordConvert {

    @ApiModelProperty("要修改的规则id")
    private Long id;
    @ApiModelProperty("删除的模糊匹配关键词列表")
    private List<String> reomveFuzzyMatchKeywordList;
    @ApiModelProperty("删除的精确匹配关键词列表")
    private List<String> removeExactMatchKeywordList;
    @ApiModelProperty("删除的标签id列表")
    private List<String> removeTagIdList;


    /**
     * 转换为修改对应标签规则entity的具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagRule toWeAutoTagRule() {
        if (this.id == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeAutoTagRule weAutoTagRule = super.convertToWeAutoTagRule();
        weAutoTagRule.setId(this.id);
        return weAutoTagRule;
    }

    @Override
    public List<WeAutoTagKeyword> toWeAutoTagKeywordList() {
        List<WeAutoTagKeyword> weAutoTagKeywordList = super.convertToWeAutoTagKeywordList();
        for (WeAutoTagKeyword weAutoTagKeyword : weAutoTagKeywordList) {
            weAutoTagKeyword.setRuleId(this.id);
        }
        return weAutoTagKeywordList;
    }

    @Override
    public List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList() {
        List<WeAutoTagKeywordTagRel> weAutoTagKeywordTagRelList = super.convertToWeAutoTagKeywordTagRelList();
        for (WeAutoTagKeywordTagRel weAutoTagKeywordTagRel : weAutoTagKeywordTagRelList) {
            weAutoTagKeywordTagRel.setRuleId(this.id);
        }
        return weAutoTagKeywordTagRelList;
    }

    @Override
    public List<WeAutoTagUserRel> toWeAutoTagUserRel(Long ruleId) {
        return super.toWeAutoTagUserRel(ruleId);
    }

}
