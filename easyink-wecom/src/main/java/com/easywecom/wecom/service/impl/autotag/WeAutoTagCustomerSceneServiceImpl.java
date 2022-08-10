package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.autotag.AutoTagCustomerSceneType;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagCustomerScene;
import com.easywecom.wecom.mapper.autotag.WeAutoTagCustomerSceneMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagCustomerSceneService;
import com.easywecom.wecom.service.autotag.WeAutoTagCustomerSceneTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新客标签场景表(WeAutoTagCustomerScene)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:31
 */
@Service("weAutoTagCustomerSceneService")
public class WeAutoTagCustomerSceneServiceImpl extends ServiceImpl<WeAutoTagCustomerSceneMapper, WeAutoTagCustomerScene> implements WeAutoTagCustomerSceneService {

    @Autowired
    private WeAutoTagCustomerSceneTagRelService weAutoTagCustomerSceneTagRelService;

    /**
     * 批量添加
     *
     * @param weAutoTagCustomerSceneList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList) {
        return this.batchSave(weAutoTagCustomerSceneList, true);
    }

    /**
     * 批量添加
     *
     * @param weAutoTagCustomerSceneList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(weAutoTagCustomerSceneList)) {
            if (insertOnly) {
                this.saveBatch(weAutoTagCustomerSceneList);
                return 1;
            }
            this.saveOrUpdateBatch(weAutoTagCustomerSceneList);
            return 1;
        }
        return 0;
    }

    /**
     * 修改
     *
     * @param removeSceneIdList
     * @param toWeAutoTagCustomerSceneList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean edit(List<Long> removeSceneIdList, List<WeAutoTagCustomerScene> toWeAutoTagCustomerSceneList) {
        // 删除场景
        this.removeBatchBySceneIdList(removeSceneIdList);
        // 删除场景下的标签
        weAutoTagCustomerSceneTagRelService.removeBySceneIdList(removeSceneIdList);
        // 修改场景
        return this.saveOrUpdateBatch(toWeAutoTagCustomerSceneList);
    }

    /**
     * 根据场景id列表删除
     *
     * @param removeSceneIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBatchBySceneIdList(List<Long> removeSceneIdList) {

        if (CollectionUtils.isNotEmpty(removeSceneIdList)) {
            return this.baseMapper.deleteBatchIds(removeSceneIdList);
        }
        return 0;
    }

    /**
     * 根据标签id列表删除新客场景
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagCustomerScene>()
                    .in(WeAutoTagCustomerScene::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 从规则id列表中获取符合条件的的新客场景id列表
     *
     * @param availableRuleIdList
     * @return
     */
    @Override
    public Map<Long, List<Long>> getAvailableSceneIdListFromRuleIdListGroupByRuleId(List<Long> availableRuleIdList) {
        Map<Long, List<Long>> availableSceneIdListGroupByRuleIdMap = new HashMap<>();
        if (CollectionUtils.isEmpty(availableRuleIdList)) {
            return availableSceneIdListGroupByRuleIdMap;
        }
        // 查询新客场景
        List<WeAutoTagCustomerScene> customerSceneList = this.list(new LambdaQueryWrapper<WeAutoTagCustomerScene>()
                .in(WeAutoTagCustomerScene::getRuleId, availableRuleIdList));

        availableSceneIdListGroupByRuleIdMap = customerSceneList.stream().filter(item -> {
            AutoTagCustomerSceneType sceneType = AutoTagCustomerSceneType.getByType(item.getSceneType());
            if (sceneType == null) {
                return false;
            }
            return sceneType.match(item.getLoopPoint(),
                    Time.valueOf(item.getLoopBeginTime()),
                    Time.valueOf(item.getLoopEndTime()),
                    Time.valueOf(DateUtils.dateTimeNow(DateUtils.HH_MM_SS)));
        }).collect(Collectors.groupingBy(WeAutoTagCustomerScene::getRuleId,
                Collectors.mapping(WeAutoTagCustomerScene::getId,
                        Collectors.toList())));
        return availableSceneIdListGroupByRuleIdMap;
    }
}

