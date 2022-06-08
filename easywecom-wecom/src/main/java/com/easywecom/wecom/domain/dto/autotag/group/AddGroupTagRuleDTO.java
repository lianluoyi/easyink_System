package com.easywecom.wecom.domain.dto.autotag.group;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.autotag.AutoTagLabelTypeEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.convert.autotag.group.AddTagRuleGroupConvert;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupScene;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 新增入群标签规则DTO
 *
 * @author tigger
 * 2022/2/28 9:07
 **/
@Slf4j
@Data
public class AddGroupTagRuleDTO extends GroupTagRuleDTO implements AddTagRuleGroupConvert {

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
        weAutoTagRule.setLabelType(AutoTagLabelTypeEnum.GROUP.getType());
        weAutoTagRule.setCreateTime(new Date());
        weAutoTagRule.setCreateBy(this.getCreateBy());
        return weAutoTagRule;
    }

    @Override
    public List<WeAutoTagGroupScene> toWeAutoTagGroupSceneList(Long ruleId, String corpId) {
        if (ruleId == null || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeAutoTagGroupScene> weAutoTagGroupSceneList = super.convertToWeAutoTagGroupSceneList();
        for (WeAutoTagGroupScene weAutoTagGroupScene : weAutoTagGroupSceneList) {
            weAutoTagGroupScene.setRuleId(ruleId);
            weAutoTagGroupScene.setCorpId(corpId);
        }
        return weAutoTagGroupSceneList;
    }

    @Override
    public List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList(List<Long> groupSceneIdList, Long ruleId) {
        final int size = this.getGroupSceneList().size();
        List<WeAutoTagGroupSceneGroupRel> allList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long groupSceneId = groupSceneIdList.get(i);
            GroupSceneDTO groupScene = this.getGroupSceneList().get(i);
            List<WeAutoTagGroupSceneGroupRel> weAutoTagGroupSceneGroupRelList =
                    super.convertToWeAutoTagGroupSceneGroupRelList(groupScene.getGroupIdList());
            for (WeAutoTagGroupSceneGroupRel weAutoTagGroupSceneGroupRel : weAutoTagGroupSceneGroupRelList) {
                weAutoTagGroupSceneGroupRel.setGroupSceneId(groupSceneId);
                weAutoTagGroupSceneGroupRel.setRuleId(ruleId);
            }
            allList.addAll(weAutoTagGroupSceneGroupRelList);
        }
        return allList;
    }

    @Override
    public List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList(List<Long> groupSceneIdList, Long ruleId) {
        final int size = this.getGroupSceneList().size();
        List<WeAutoTagGroupSceneTagRel> allList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long groupSceneId = groupSceneIdList.get(i);
            GroupSceneDTO groupScene = this.getGroupSceneList().get(i);
            List<WeAutoTagGroupSceneTagRel> weAutoTagGroupSceneTagRelList =
                    super.convertToWeAutoTagGroupSceneTagRelList(groupScene.getTagIdList());
            for (WeAutoTagGroupSceneTagRel weAutoTagGroupSceneTagRel : weAutoTagGroupSceneTagRelList) {
                weAutoTagGroupSceneTagRel.setGroupSceneId(groupSceneId);
                weAutoTagGroupSceneTagRel.setRuleId(ruleId);
            }
            allList.addAll(weAutoTagGroupSceneTagRelList);
        }
        return allList;
    }
}
