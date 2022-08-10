package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.dto.autotag.group.GroupSceneDTO;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;
import com.easywecom.wecom.mapper.autotag.WeAutoTagGroupSceneTagRelMapper;
import com.easywecom.wecom.service.WeTagService;
import com.easywecom.wecom.service.autotag.WeAutoTagGroupSceneTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 群标签场景与标签关系表(WeAutoTagGroupSceneTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:36
 */
@Service("weAutoTagGroupSceneTagRelService")
public class WeAutoTagGroupSceneTagRelServiceImpl extends ServiceImpl<WeAutoTagGroupSceneTagRelMapper, WeAutoTagGroupSceneTagRel> implements WeAutoTagGroupSceneTagRelService {

    @Autowired
    private WeTagService weTagService;

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneTagRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList) {
        return this.batchSave(toWeAutoTagGroupSceneTagRelList, true);
    }

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneTagRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(toWeAutoTagGroupSceneTagRelList)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(toWeAutoTagGroupSceneTagRelList);
            }
            return this.baseMapper.insertOrUpdateBatch(toWeAutoTagGroupSceneTagRelList);
        }
        return 0;
    }

    /**
     * 根据场景id列表删除场景标签
     *
     * @param removeSceneIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBatchBySceneIdList(List<Long> removeSceneIdList) {
        if (CollectionUtils.isNotEmpty(removeSceneIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupSceneTagRel>()
                    .in(WeAutoTagGroupSceneTagRel::getGroupSceneId, removeSceneIdList));
        }
        return 0;
    }

    /**
     * 修改群场景的标签关系
     *
     * @param groupSceneList
     * @param toWeAutoTagGroupSceneTagRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(List<GroupSceneDTO> groupSceneList, List<WeAutoTagGroupSceneTagRel> toWeAutoTagGroupSceneTagRelList) {
        for (GroupSceneDTO groupSceneDTO : groupSceneList) {
            Long groupSceneId = groupSceneDTO.getId();
            List<String> removeTagIdList = groupSceneDTO.getRemoveTagIdList();
            if (groupSceneId != null && CollectionUtils.isNotEmpty(removeTagIdList)) {
                // 删除
                this.removeBySceneIdAndTagIdList(groupSceneId, removeTagIdList);
            }
        }
        // 添加或修改
        return this.batchSave(toWeAutoTagGroupSceneTagRelList, false);
    }

    /**
     * 删除指定场景下的标签Id列表
     *
     * @param groupSceneId
     * @param removeTagIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBySceneIdAndTagIdList(Long groupSceneId, List<String> removeTagIdList) {
        if (groupSceneId == null) {
            log.error("群场景id为null");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isNotEmpty(removeTagIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupSceneTagRel>()
                    .eq(WeAutoTagGroupSceneTagRel::getGroupSceneId, groupSceneId)
                    .in(WeAutoTagGroupSceneTagRel::getTagId, removeTagIdList));
        }
        return 0;
    }

    /**
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupSceneTagRel>()
                    .in(WeAutoTagGroupSceneTagRel::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 获取标签列表通过群场景id
     *
     * @param sceneIdList 群场景id列表
     * @return
     */
    @Override
    public List<WeTag> getTagListBySceneIdList(List<Long> sceneIdList) {

        List<WeAutoTagGroupSceneTagRel> tagRelList = this.list(new LambdaQueryWrapper<WeAutoTagGroupSceneTagRel>()
                .in(WeAutoTagGroupSceneTagRel::getGroupSceneId, sceneIdList));
        Set<String> tagIdsSet = tagRelList.stream().map(WeAutoTagGroupSceneTagRel::getTagId).collect(Collectors.toSet());

        return weTagService.list(new LambdaQueryWrapper<WeTag>()
                .in(WeTag::getTagId, tagIdsSet));
    }



}

