package com.easywecom.wecom.domain.vo.autotag.keyword;

import com.easywecom.wecom.domain.vo.autotag.TagInfoVO;
import com.easywecom.wecom.domain.vo.autotag.TagRuleUserListVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 关键词规则详情
 *
 * @author tigger
 * 2022/2/28 15:03
 **/
@Data
public class TagRuleKeywordInfoVO extends TagRuleUserListVO {

    @ApiModelProperty("模糊匹配关键词列表")
    private List<String> fuzzyMatchKeywordList;
    @ApiModelProperty("精确匹配关键词列表")
    private List<String> exactMatchKeywordList;
    @ApiModelProperty("标签名称列表")
    private List<TagInfoVO> tagList;
}
