package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerScene;

import java.util.List;
import java.util.Map;

/**
 * 新客标签场景表(WeAutoTagCustomerScene)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:30
 */
public interface WeAutoTagCustomerSceneService extends IService<WeAutoTagCustomerScene> {

    /**
     * 批量添加
     *
     * @param weAutoTagCustomerSceneList
     * @return
     */
    int batchSave(List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList);

    /**
     * 批量添加
     *
     * @param weAutoTagCustomerSceneList
     * @param inertOnly
     * @return
     */
    int batchSave(List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList, Boolean inertOnly);

    /**
     * 修改
     *
     * @param removeSceneIdList
     * @param toWeAutoTagCustomerSceneList
     * @return
     */
    Boolean edit(List<Long> removeSceneIdList, List<WeAutoTagCustomerScene> toWeAutoTagCustomerSceneList);

    /**
     * 根据场景id列表删除
     *
     * @param removeSceneIdList
     * @return
     */
    int removeBatchBySceneIdList(List<Long> removeSceneIdList);

    /**
     * 根据标签id列表删除新客场景
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);

    /**
     * 从规则id列表中获取符合条件的的新客场景id列表
     *
     * @param availableRuleIdList
     * @return
     */
    Map<Long, List<Long>> getAvailableSceneIdListFromRuleIdListGroupByRuleId(List<Long> availableRuleIdList);
}

