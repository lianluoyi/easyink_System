package com.easywecom.wecom.domain.dto.autotag.keyword;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.autotag.AutoTagLabelTypeEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.convert.autotag.keyword.AddTagRuleKeywordConvert;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRule;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * 添加关键词标签规则DTO
 *
 * @author tigger
 * 2022/2/27 16:50
 **/
@Slf4j
@Data
public class AddKeywordTagRuleDTO extends KeywordTagRuleDTO implements AddTagRuleKeywordConvert {
    @ApiModelProperty(value = "企业id", hidden = true)
    private String corpId;
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    /**
     * 转换为新增对应的标签规则entity具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagRule toWeAutoTagRule() {
        WeAutoTagRule weAutoTagRule = super.convertToWeAutoTagRule();
        weAutoTagRule.setCorpId(this.getCorpId());
        weAutoTagRule.setLabelType(AutoTagLabelTypeEnum.KEYWORD.getType());
        weAutoTagRule.setCreateTime(new Date());
        weAutoTagRule.setCreateBy(this.getCreateBy());
        return weAutoTagRule;
    }

    @Override
    public List<WeAutoTagKeyword> toWeAutoTagKeywordList(Long ruleId) {
        if (ruleId == null) {
            log.error("规则不能为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeAutoTagKeyword> weAutoTagKeywordList = super.convertToWeAutoTagKeywordList();
        for (WeAutoTagKeyword weAutoTagKeyword : weAutoTagKeywordList) {
            weAutoTagKeyword.setRuleId(ruleId);
        }
        return weAutoTagKeywordList;
    }

    @Override
    public List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList(Long ruleId) {
        if (ruleId == null) {
            log.error("规则不能为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeAutoTagKeywordTagRel> weAutoTagKeywordTagRelList = super.convertToWeAutoTagKeywordTagRelList();
        for (WeAutoTagKeywordTagRel weAutoTagKeywordTagRel : weAutoTagKeywordTagRelList) {
            weAutoTagKeywordTagRel.setRuleId(ruleId);
        }
        return weAutoTagKeywordTagRelList;
    }

    @Override
    public List<WeAutoTagUserRel> toWeAutoTagUserRel(Long ruleId) {
        return super.toWeAutoTagUserRel(ruleId);
    }
}
