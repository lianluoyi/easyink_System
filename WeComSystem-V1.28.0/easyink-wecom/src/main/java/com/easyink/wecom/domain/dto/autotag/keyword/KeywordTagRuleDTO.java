package com.easyink.wecom.domain.dto.autotag.keyword;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.autotag.AutoTagMatchTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.convert.autotag.TagRuleUserRelConvert;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 关键词标签规则DTO
 *
 * @author tigger
 * 2022/2/28 9:18
 **/
@Slf4j
@Data
public class KeywordTagRuleDTO extends AbstractKeywordTagRuleDTO implements TagRuleUserRelConvert {
    @ApiModelProperty("模糊匹配关键词列表")
    private List<String> fuzzyMatchKeywordList;
    @ApiModelProperty("精确匹配关键词列表")
    private List<String> exactMatchKeywordList;
    @ApiModelProperty("标签id列表")
    private List<String> tagIdList;


    @Override
    public List<WeAutoTagKeyword> convertToWeAutoTagKeywordList() {
        if (CollectionUtils.isEmpty(this.fuzzyMatchKeywordList) && CollectionUtils.isEmpty(this.exactMatchKeywordList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_MATCH_KEYWORD_NOT_BOTH_NULL);
        }
        // 校验数量
        if (this.fuzzyMatchKeywordList.size() > WeConstans.AUTO_TAG_KEYWORD_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_KEYWORD_NUM_LIMIT);
        }
        if (this.exactMatchKeywordList.size() > WeConstans.AUTO_TAG_KEYWORD_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_KEYWORD_NUM_LIMIT);
        }
        // 校验关键词重复

        if (checkKeywordRepeated(this.fuzzyMatchKeywordList, this.exactMatchKeywordList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_KEYWORD_REPEATED);
        }
        List<WeAutoTagKeyword> weAutoTagKeywordList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(this.fuzzyMatchKeywordList)) {
            for (String fuzzyKeyword : this.fuzzyMatchKeywordList) {
                weAutoTagKeywordList.add(new WeAutoTagKeyword(AutoTagMatchTypeEnum.FUZZY.getType(), fuzzyKeyword));
            }
        }
        if (CollectionUtils.isNotEmpty(this.exactMatchKeywordList)) {
            for (String exactKeyword : this.exactMatchKeywordList) {
                weAutoTagKeywordList.add(new WeAutoTagKeyword(AutoTagMatchTypeEnum.EXACT.getType(), exactKeyword));
            }
        }
        return weAutoTagKeywordList;
    }

    /**
     * 校验关键词重复
     *
     * @param fuzzyMatchKeywordList
     * @param exactMatchKeywordList
     * @return
     */
    private boolean checkKeywordRepeated(List<String> fuzzyMatchKeywordList, List<String> exactMatchKeywordList) {
        if (CollectionUtils.isNotEmpty(fuzzyMatchKeywordList)) {
            return fuzzyMatchKeywordList.size() != fuzzyMatchKeywordList.stream().distinct().count();
        }
        if (CollectionUtils.isNotEmpty(exactMatchKeywordList)) {
            return exactMatchKeywordList.size() != exactMatchKeywordList.stream().distinct().count();
        }
        return false;
    }


    @Override
    public List<WeAutoTagKeywordTagRel> convertToWeAutoTagKeywordTagRelList() {
        if (CollectionUtils.isEmpty(this.tagIdList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_TAG_NOT_NULL);
        }
        if (this.tagIdList.size() > WeConstans.AUTO_TAG_TAG_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_TAG_NUM_LIMIT);
        }
        List<WeAutoTagKeywordTagRel> weAutoTagKeywordTagRelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(this.getTagIdList())) {
            for (String tagId : this.getTagIdList()) {
                weAutoTagKeywordTagRelList.add(new WeAutoTagKeywordTagRel(tagId));
            }
        }
        return weAutoTagKeywordTagRelList;
    }

    @Override
    public List<WeAutoTagUserRel> toWeAutoTagUserRel(Long ruleId) {
        return super.convertToWeAutoTagUserRelList(ruleId);
    }


}
