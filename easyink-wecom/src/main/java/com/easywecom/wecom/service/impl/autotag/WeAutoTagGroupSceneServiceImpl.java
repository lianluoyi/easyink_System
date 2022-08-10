package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupScene;
import com.easywecom.wecom.mapper.autotag.WeAutoTagGroupSceneMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagGroupSceneGroupRelService;
import com.easywecom.wecom.service.autotag.WeAutoTagGroupSceneService;
import com.easywecom.wecom.service.autotag.WeAutoTagGroupSceneTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 群标签场景表(WeAutoTagGroupScene)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:33
 */
@Service("weAutoTagGroupSceneService")
public class WeAutoTagGroupSceneServiceImpl extends ServiceImpl<WeAutoTagGroupSceneMapper, WeAutoTagGroupScene> implements WeAutoTagGroupSceneService {

    @Autowired
    private WeAutoTagGroupSceneGroupRelService weAutoTagGroupSceneGroupRelService;
    @Autowired
    private WeAutoTagGroupSceneTagRelService weAutoTagGroupSceneTagRelService;

    /**
     * 批量添加
     *
     * @param weAutoTagGroupSceneList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagGroupScene> weAutoTagGroupSceneList) {
        return this.batchSave(weAutoTagGroupSceneList, true);
    }

    /**
     * 批量添加
     *
     * @param weAutoTagGroupSceneList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagGroupScene> weAutoTagGroupSceneList, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(weAutoTagGroupSceneList)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(weAutoTagGroupSceneList);
            }
            return this.baseMapper.insertOrUpdateBatch(weAutoTagGroupSceneList);
        }
        return 0;
    }

    /**
     * 修改
     *
     * @param removeSceneIdList
     * @param weAutoTagGroupSceneList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean edit(List<Long> removeSceneIdList, List<WeAutoTagGroupScene> weAutoTagGroupSceneList) {
        // 删除场景
        this.removeBatchBySceneIdList(removeSceneIdList);

        // 删除场景下的群关系
        weAutoTagGroupSceneGroupRelService.removeBatchBySceneIdList(removeSceneIdList);
        // 删除场景下的标签关系
        weAutoTagGroupSceneTagRelService.removeBatchBySceneIdList(removeSceneIdList);
        // 修改场景
        return this.saveOrUpdateBatch(weAutoTagGroupSceneList);
    }

    /**
     * 根据场景idList删除场景
     *
     * @param removeSceneIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBatchBySceneIdList(List<Long> removeSceneIdList) {
        if (CollectionUtils.isNotEmpty(removeSceneIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupScene>()
                    .in(WeAutoTagGroupScene::getId, removeSceneIdList));
        }
        return 0;
    }

    /**
     * 根据规则id列表删除场景
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupScene>()
                    .in(WeAutoTagGroupScene::getRuleId, removeRuleIdList));
        }
        return 0;
    }
}

