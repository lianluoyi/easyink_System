package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupScene;

import java.util.List;

/**
 * 群标签场景表(WeAutoTagGroupScene)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:33
 */
public interface WeAutoTagGroupSceneService extends IService<WeAutoTagGroupScene> {

    /**
     * 批量添加
     *
     * @param weAutoTagGroupSceneList
     */
    int batchSave(List<WeAutoTagGroupScene> weAutoTagGroupSceneList);

    /**
     * 批量添加
     *
     * @param weAutoTagGroupSceneList
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagGroupScene> weAutoTagGroupSceneList, Boolean insertOnly);

    /**
     * 修改
     *
     * @param removeSceneIdList
     * @param weAutoTagGroupSceneList
     * @return
     */
    Boolean edit(List<Long> removeSceneIdList, List<WeAutoTagGroupScene> weAutoTagGroupSceneList);

    /**
     * 根据场景idList删除场景
     *
     * @param removeSceneIdList
     * @return
     */
    int removeBatchBySceneIdList(List<Long> removeSceneIdList);

    /**
     * 根据规则id列表删除场景
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);
}

