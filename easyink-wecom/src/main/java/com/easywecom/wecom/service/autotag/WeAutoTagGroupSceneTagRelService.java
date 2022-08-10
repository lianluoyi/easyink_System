package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.dto.autotag.group.GroupSceneDTO;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;

import java.util.List;

/**
 * 群标签场景与标签关系表(WeAutoTagGroupSceneTagRel)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:36
 */
public interface WeAutoTagGroupSceneTagRelService extends IService<WeAutoTagGroupSceneTagRel> {

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneTagRelList
     * @return
     */
    int batchSave(List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList);

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneTagRelList
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList, Boolean insertOnly);

    /**
     * 根据场景id列表删除场景标签
     *
     * @param removeSceneIdList
     * @return
     */
    int removeBatchBySceneIdList(List<Long> removeSceneIdList);

    /**
     * 修改群场景的标签关系
     *
     * @param groupSceneList
     * @param toWeAutoTagGroupSceneTagRelList
     * @return
     */
    int edit(List<GroupSceneDTO> groupSceneList, List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList);

    /**
     * 删除指定场景下的标签Id列表
     *
     * @param groupSceneId
     * @param removeTagIdList
     * @return
     */
    int removeBySceneIdAndTagIdList(Long groupSceneId, List<String> removeTagIdList);

    /**
     * 根据规则id列表删除场景标签信息
     *
     * @param removeRuleIdList
     * @return
     */
    int removeRuleIdList(List<Long> removeRuleIdList);

    /**
     * 获取标签列表通过群场景id
     *
     * @param sceneIdList 群场景id列表
     * @return
     */
    List<WeTag> getTagListBySceneIdList(List<Long> sceneIdList);


}

