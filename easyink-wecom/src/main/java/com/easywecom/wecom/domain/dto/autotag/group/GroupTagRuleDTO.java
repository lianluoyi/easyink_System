package com.easywecom.wecom.domain.dto.autotag.group;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupScene;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.easywecom.common.constant.WeConstans.*;

/**
 * 群标签规则DTO
 *
 * @author tigger
 * 2022/2/28 9:14
 **/
@Data
public class GroupTagRuleDTO extends AbstractGroupTagRuleDTO {

    @ApiModelProperty("群场景列表")
    List<GroupSceneDTO> groupSceneList;

    @Override
    public List<WeAutoTagGroupScene> convertToWeAutoTagGroupSceneList() {
        // 校验
        if (CollectionUtils.isEmpty(this.groupSceneList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_NOT_NULL);
        }
        if (this.groupSceneList.size() > AUTO_TAG_GROUP_SCENE_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_GROUP_SCENE_NUM_LIMIT);
        }

        List<WeAutoTagGroupScene> weAutoTagGroupSceneList = new ArrayList<>();
        for (GroupSceneDTO groupScene : groupSceneList) {
            weAutoTagGroupSceneList.add(new WeAutoTagGroupScene(groupScene.getId()));
        }
        return weAutoTagGroupSceneList;
    }

    @Override
    public List<WeAutoTagGroupSceneGroupRel> convertToWeAutoTagGroupSceneGroupRelList(List<String> groupIdList) {
        if (CollectionUtils.isEmpty(groupIdList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_GROUP_NOT_NULL);
        }
        if (groupIdList.size() > AUTO_TAG_GROUP_SCENE_GROUP_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_GROUP_NUM_LIMIT);
        }
        List<WeAutoTagGroupSceneGroupRel> weAutoTagGroupSceneGroupRelList = new ArrayList<>();
        for (String groupId : groupIdList) {
            weAutoTagGroupSceneGroupRelList.add(new WeAutoTagGroupSceneGroupRel(groupId));
        }
        return weAutoTagGroupSceneGroupRelList;
    }

    @Override
    public List<WeAutoTagGroupSceneTagRel> convertToWeAutoTagGroupSceneTagRelList(List<String> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_TAG_NOT_NULL);
        }
        if (tagIdList.size() > AUTO_TAG_GROUP_SCENE_TAG_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_TAG_NUM_LIMIT);
        }
        List<WeAutoTagGroupSceneTagRel> weAutoTagGroupSceneTagRelList = new ArrayList<>();
        for (String tagId : tagIdList) {
            weAutoTagGroupSceneTagRelList.add(new WeAutoTagGroupSceneTagRel(tagId));
        }
        return weAutoTagGroupSceneTagRelList;
    }
}
