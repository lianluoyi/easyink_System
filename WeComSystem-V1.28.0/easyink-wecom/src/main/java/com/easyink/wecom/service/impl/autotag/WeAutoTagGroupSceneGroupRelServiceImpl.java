package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.autotag.group.GroupSceneDTO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;
import com.easyink.wecom.domain.vo.autotag.group.GroupSceneRuleVO;
import com.easyink.wecom.mapper.autotag.WeAutoTagGroupSceneGroupRelMapper;
import com.easyink.wecom.service.autotag.WeAutoTagGroupSceneGroupRelService;
import com.easyink.wecom.service.autotag.WeAutoTagGroupSceneTagRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群标签场景与群关系表(WeAutoTagGroupSceneGroupRel)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:35
 */
@Slf4j
@Service("weAutoTagGroupSceneGroupRelService")
public class WeAutoTagGroupSceneGroupRelServiceImpl extends ServiceImpl<WeAutoTagGroupSceneGroupRelMapper, WeAutoTagGroupSceneGroupRel> implements WeAutoTagGroupSceneGroupRelService {

    @Autowired
    private WeAutoTagGroupSceneTagRelService weAutoTagGroupSceneTagRelService;

    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneGroupRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList) {
        return this.batchSave(toWeAutoTagGroupSceneGroupRelList, true);
    }


    /**
     * 批量添加
     *
     * @param toWeAutoTagGroupSceneGroupRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(toWeAutoTagGroupSceneGroupRelList)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(toWeAutoTagGroupSceneGroupRelList);

            }
            return this.baseMapper.insertOrUpdateBatch(toWeAutoTagGroupSceneGroupRelList);
        }
        return 0;
    }

    /**
     * 修改群场景的群关系
     *
     * @param groupSceneList
     * @param toWeAutoTagGroupSceneGroupRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(List<GroupSceneDTO> groupSceneList, List<WeAutoTagGroupSceneGroupRel> toWeAutoTagGroupSceneGroupRelList) {
        for (GroupSceneDTO groupSceneDTO : groupSceneList) {
            Long groupSceneId = groupSceneDTO.getId();
            List<String> removeGroupIdList = groupSceneDTO.getRemoveGroupIdList();
            if (groupSceneId != null && CollectionUtils.isNotEmpty(removeGroupIdList)) {
                // 删除
                this.removeBySceneIdAndGroupIdList(groupSceneId, removeGroupIdList);
            }
        }

        // 添加或修改
        return this.batchSave(toWeAutoTagGroupSceneGroupRelList, false);
    }

    /**
     * 删除群场景群关系根据群场景id列表
     *
     * @param removeSceneIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBatchBySceneIdList(List<Long> removeSceneIdList) {
        if (CollectionUtils.isNotEmpty(removeSceneIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupSceneGroupRel>()
                    .in(WeAutoTagGroupSceneGroupRel::getGroupSceneId, removeSceneIdList));
        }
        return 0;
    }

    /**
     * 删除指定场景下的群Id列表
     *
     * @param groupSceneId
     * @param removeGroupIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBySceneIdAndGroupIdList(Long groupSceneId, List<String> removeGroupIdList) {
        if (groupSceneId == null) {
            log.error("群场景id为null");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isNotEmpty(removeGroupIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupSceneGroupRel>()
                    .eq(WeAutoTagGroupSceneGroupRel::getGroupSceneId, groupSceneId)
                    .in(WeAutoTagGroupSceneGroupRel::getGroupId, removeGroupIdList));
        }
        return 0;
    }

    /**
     * 根据规则id列表删除场景群信息
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagGroupSceneGroupRel>()
                    .in(WeAutoTagGroupSceneGroupRel::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 查询包含该群id的场景id列表
     * 群id是唯一的,不用加corpId
     *
     * @param chatId 群id
     * @return
     */
    @Override
    public Map<Long, List<Long>> getSceneIdListGroupByRuleIdByChatId(String chatId) {
        List<WeAutoTagGroupSceneGroupRel> weAutoTagGroupSceneGroupRelList = this.baseMapper.selectList(new LambdaQueryWrapper<WeAutoTagGroupSceneGroupRel>()
                .eq(WeAutoTagGroupSceneGroupRel::getGroupId, chatId));

        return CollectionUtils.isNotEmpty(weAutoTagGroupSceneGroupRelList) ? weAutoTagGroupSceneGroupRelList.stream()
                .collect(Collectors.groupingBy(WeAutoTagGroupSceneGroupRel::getRuleId,
                        Collectors.mapping(WeAutoTagGroupSceneGroupRel::getGroupSceneId,
                                Collectors.toList()))) : new HashMap<>();
    }

    /**
     * 查询标签列表根据群id
     *
     * @param corpId
     * @param chatId 群id
     * @return
     */
    @Override
    public Map<Long, List<WeTag>> getTagListGroupByRuleIdByChatId(String corpId, String chatId) {
        Map<Long, List<WeTag>> ruleTagMap = new HashMap<>();
        if (StringUtils.isBlank(chatId)) {
            log.error("参数异常,跳过打标签,chatId: {}", chatId);
            return ruleTagMap;
        }


        // 查询包含该群的规则id列表与场景id的映射
        List<GroupSceneRuleVO> sceneIdAndRuleIdMapping = this.baseMapper.selectRuleIdAndSceneIdMappingByChatId(corpId,chatId);
        if (CollectionUtils.isEmpty(sceneIdAndRuleIdMapping)) {
            return ruleTagMap;
        }
        // 转换成map k: 规则id, v:场景id列表
        Map<Long, List<Long>> groupByRuleIdMap = sceneIdAndRuleIdMapping.stream()
                .collect(Collectors.groupingBy(GroupSceneRuleVO::getRuleId,
                        Collectors.mapping(GroupSceneRuleVO::getGroupSceneId, Collectors.toList())));

        // 保存规则对应触发的标签列表
        for (Map.Entry<Long, List<Long>> entry : groupByRuleIdMap.entrySet()) {
            Long ruleId = entry.getKey();
            List<Long> sceneIdList = entry.getValue();
            List<WeTag> weTagList = weAutoTagGroupSceneTagRelService.getTagListBySceneIdList(sceneIdList);
            ruleTagMap.put(ruleId, weTagList);
        }
        return ruleTagMap;
    }
}

