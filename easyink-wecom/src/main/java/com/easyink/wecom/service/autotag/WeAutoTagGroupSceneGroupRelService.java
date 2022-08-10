package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.autotag.group.GroupSceneDTO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;

import java.util.List;
import java.util.Map;

/**
 * 群标签场景与群关系表(WeAutoTagGroupSceneGroupRel)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:34
 */
public interface WeAutoTagGroupSceneGroupRelService extends IService<WeAutoTagGroupSceneGroupRel> {

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneGroupRelList
     * @return
     */
    int batchSave(List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList);

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneGroupRelList
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList, Boolean insertOnly);

    /**
     * 修改群场景群关系
     *
     * @param groupSceneList
     * @param toWeAutoTagGroupSceneGroupRelList
     * @return
     */
    int edit(List<GroupSceneDTO> groupSceneList, List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList);

    /**
     * 删除群场景群关系根据群场景id列表
     *
     * @param removeSceneIdList
     * @return
     */
    int removeBatchBySceneIdList(List<Long> removeSceneIdList);

    /**
     * 删除指定场景下的群Id列表
     *
     * @param groupSceneId
     * @param removeGroupIdList
     * @return
     */
    int removeBySceneIdAndGroupIdList(Long groupSceneId, List<String> removeGroupIdList);

    /**
     * 根据规则id列表删除场景群信息
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);


    /**
     * 查询包含该群id的场景id列表(SceneIdList)
     *
     * @param chatId 群id
     * @return
     */
    Map<Long, List<Long>> getSceneIdListGroupByRuleIdByChatId(String chatId);

    /**
     * 查询标签列表根据群id
     *
     * @param chatId
     * @return
     */
    Map<Long, List<WeTag>> getTagListGroupByRuleIdByChatId(String chatId);
}

