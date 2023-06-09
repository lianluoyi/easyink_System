package com.easyink.wecom.domain.vo.autotag.record.keyword;

import com.easyink.wecom.domain.vo.autotag.record.TagRuleRecordVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 关键词标签规则记录VO
 *
 * @author tigger
 * 2022/3/1 17:05
 **/
@Data
public class KeywordTagRuleRecordVO extends TagRuleRecordVO {

    @ApiModelProperty("触发的关键词数量")
    private Integer hitCount;


}
