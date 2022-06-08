package com.easywecom.wecom.domain.vo.autotag.group;

import com.easywecom.wecom.domain.vo.autotag.TagRuleInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 群规则详情
 *
 * @author tigger
 * 2022/2/28 15:06
 **/
@Data
public class TagRuleGroupInfoVO extends TagRuleInfoVO {

    @ApiModelProperty("群场景列表")
    private List<GroupSceneVO> groupSceneList;


}
