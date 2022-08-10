package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;
import com.easyink.wecom.mapper.autotag.WeAutoTagKeywordTagRelMapper;
import com.easyink.wecom.service.WeTagService;
import com.easyink.wecom.service.autotag.WeAutoTagKeywordTagRelService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 关键词与标签关系表(WeAutoTagKeywordTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:39
 */
@Slf4j
@Service("weAutoTagKeywordTagRelService")
public class WeAutoTagKeywordTagRelServiceImpl extends ServiceImpl<WeAutoTagKeywordTagRelMapper, WeAutoTagKeywordTagRel> implements WeAutoTagKeywordTagRelService {

    @Autowired
    private WeTagService weTagService;

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordTagRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList) {
        return this.batchSave(toWeAutoTagKeywordTagRelList, true);
    }

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordTagRelList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList, Boolean insertOnly) {
        if (CollectionUtils.isNotEmpty(toWeAutoTagKeywordTagRelList)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(toWeAutoTagKeywordTagRelList);
            }
            return this.baseMapper.insertOrUpdateBatch(toWeAutoTagKeywordTagRelList);
        }
        return 0;
    }

    /**
     * 修改
     *
     * @param removeTagIdList
     * @param toWeAutoTagKeywordTagRelList
     * @param ruleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(List<String> removeTagIdList, List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList, Long ruleId) {
        // 删除
        this.removeBatchByRuleIdAndTagIdList(removeTagIdList, ruleId);
        // 新增或修改
        return this.batchSave(toWeAutoTagKeywordTagRelList, false);
    }

    /**
     * 删除指定规则id下的标签列表
     *
     * @param removeTagIdList
     * @param ruleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeBatchByRuleIdAndTagIdList(List<String> removeTagIdList, Long ruleId) {
        if (ruleId == null) {
            log.error("ruleId为空");
        }
        if (CollectionUtils.isNotEmpty(removeTagIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagKeywordTagRel>()
                    .eq(WeAutoTagKeywordTagRel::getRuleId, ruleId)
                    .in(WeAutoTagKeywordTagRel::getTagId, removeTagIdList));
        }
        return 0;
    }

    /**
     * 根据规则id列表删除关键词语标签
     *
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {

        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagKeywordTagRel>()
                    .in(WeAutoTagKeywordTagRel::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 根据规则id列表获取标签列表
     *
     * @param matchedRuleIdSet
     * @return
     */
    @Override
    public List<WeTag> getTagListByRuleIdList(Set<Long> matchedRuleIdSet) {
        if (CollectionUtils.isEmpty(matchedRuleIdSet)) {
            log.info("无匹配到的规则id列表");
            return Lists.newArrayList();
        }
        Set<String> tagIdSet = this.list(new LambdaQueryWrapper<WeAutoTagKeywordTagRel>()
                .in(WeAutoTagKeywordTagRel::getRuleId, matchedRuleIdSet)).stream().map(WeAutoTagKeywordTagRel::getTagId).collect(Collectors.toSet());
        return weTagService.list(new LambdaQueryWrapper<WeTag>()
                .in(WeTag::getTagId, tagIdSet));
    }
}

