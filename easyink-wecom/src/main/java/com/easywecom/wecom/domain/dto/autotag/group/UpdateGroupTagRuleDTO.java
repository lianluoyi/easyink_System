package com.easywecom.wecom.domain.dto.autotag.group;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.convert.autotag.group.UpdateTagRuleGroupConvert;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupScene;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改群标签规则DTO
 *
 * @author tigger
 * 2022/2/28 9:30
 **/
@Slf4j
@Data
public class UpdateGroupTagRuleDTO extends GroupTagRuleDTO implements UpdateTagRuleGroupConvert {
    @ApiModelProperty("要修改的规则id")
    private Long id;
    @ApiModelProperty("要删除的场景id列表")
    private List<Long> removeSceneIdList;

    /**
     * 转换为修改对应标签规则entity的具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagRule toWeAutoTagRule() {
        if (id == null) {
            log.error("要修改的规则id为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeAutoTagRule weAutoTagRule = super.convertToWeAutoTagRule();
        weAutoTagRule.setId(this.id);
        return weAutoTagRule;
    }

    @Override
    public List<WeAutoTagGroupScene> toWeAutoTagGroupSceneList(Long ruleId, String corpId) {
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
