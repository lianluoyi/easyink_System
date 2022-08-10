package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.autotag.AutoTagMatchTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;
import com.easyink.wecom.mapper.autotag.WeAutoTagKeywordMapper;
import com.easyink.wecom.service.autotag.WeAutoTagKeywordService;
import com.easyink.wecom.service.autotag.WeAutoTagUserRelService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 关键词规则表(WeAutoTagKeyword)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:38
 */
@Service("weAutoTagKeywordService")
public class WeAutoTagKeywordServiceImpl extends ServiceImpl<WeAutoTagKeywordMapper, WeAutoTagKeyword> implements WeAutoTagKeywordService {

    @Autowired
    private WeAutoTagUserRelService weAutoTagUserRelService;

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagKeyword> toWeAutoTagKeywordList) {
        return this.batchSave(toWeAutoTagKeywordList, true);
    }

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordList
     * @param insertOnly
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int batchSave(List<WeAutoTagKeyword> toWeAutoTagKeywordList, Boolean insertOnly) {

        if (CollectionUtils.isNotEmpty(toWeAutoTagKeywordList)) {
            if (insertOnly) {
                return this.baseMapper.insertBatch(toWeAutoTagKeywordList);
            }
            return this.baseMapper.insertOrUpdateBatch(toWeAutoTagKeywordList);
        }
        return 0;
    }

    /**
     * 修改关键词
     *
     * @param reomveFuzzyMatchKeywordList
     * @param removeExactMatchKeywordList
     * @param toWeAutoTagKeywordList
     * @param ruleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(List<String> reomveFuzzyMatchKeywordList, List<String> removeExactMatchKeywordList,
                    List<WeAutoTagKeyword> toWeAutoTagKeywordList, Long ruleId) {
        // 删除模糊匹配关键词语
        this.removeFuzzyKeywordByRuleIdAndKeywordList(reomveFuzzyMatchKeywordList, ruleId);
        // 删除精确匹配关键词
        this.removeExactKeywordByRuleIdAndKeywordList(removeExactMatchKeywordList, ruleId);
        // 修改或新增
        return this.batchSave(toWeAutoTagKeywordList, false);
    }

    /**
     * 删除模糊匹配关键字
     *
     * @param reomveFuzzyMatchKeywordList
     * @param ruleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeFuzzyKeywordByRuleIdAndKeywordList(List<String> reomveFuzzyMatchKeywordList, Long ruleId) {
        if (ruleId == null) {
            log.error("ruleId为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        if (CollectionUtils.isNotEmpty(reomveFuzzyMatchKeywordList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagKeyword>()
                    .eq(WeAutoTagKeyword::getRuleId, ruleId)
                    .eq(WeAutoTagKeyword::getMatchType, AutoTagMatchTypeEnum.FUZZY.getType())
                    .in(WeAutoTagKeyword::getKeyword, reomveFuzzyMatchKeywordList));
        }
        return 0;
    }

    /**
     * 删除精确匹配关键字
     *
     * @param removeExactMatchKeywordList
     * @param ruleId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeExactKeywordByRuleIdAndKeywordList(List<String> removeExactMatchKeywordList, Long ruleId) {
        if (ruleId == null) {
            log.error("ruleId为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isNotEmpty(removeExactMatchKeywordList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagKeyword>()
                    .eq(WeAutoTagKeyword::getRuleId, ruleId)
                    .eq(WeAutoTagKeyword::getMatchType, AutoTagMatchTypeEnum.EXACT.getType())
                    .in(WeAutoTagKeyword::getKeyword, removeExactMatchKeywordList));
        }
        return 0;
    }

    /**
     * @param removeRuleIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByRuleIdList(List<Long> removeRuleIdList) {
        if (CollectionUtils.isNotEmpty(removeRuleIdList)) {
            return this.baseMapper.delete(new LambdaQueryWrapper<WeAutoTagKeyword>()
                    .in(WeAutoTagKeyword::getRuleId, removeRuleIdList));
        }
        return 0;
    }

    /**
     * 根据规则id分组
     *
     * @param notHadUserScopeRuleIdList
     * @return
     */
    @Override
    public Map<Long, List<WeAutoTagKeyword>> listKeywordGroupByRuleIdInRuleIdList(List<Long> notHadUserScopeRuleIdList) {
        if (CollectionUtils.isEmpty(notHadUserScopeRuleIdList)) {
            return Maps.newHashMap();
        }
        return this.list(new LambdaQueryWrapper<WeAutoTagKeyword>()
                .in(WeAutoTagKeyword::getRuleId, notHadUserScopeRuleIdList)).stream()
                .collect(Collectors.groupingBy(WeAutoTagKeyword::getRuleId));
    }

    /**
     * 从规则集合中查询关键词规则并根据规则id分组
     *
     * @param userId               员工id
     * @param hadUserScopeRuleIdList 规则集合
     * @return
     */
    @Override
    public Map<Long, List<WeAutoTagKeyword>> listKeywordGroupByRuleIdByUserId(String corpId,String userId, List<Long> hadUserScopeRuleIdList) {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(hadUserScopeRuleIdList)) {
            return Maps.newHashMap();
        }
        //查询员工
        List<WeAutoTagUserRel> list = weAutoTagUserRelService.list(new LambdaQueryWrapper<WeAutoTagUserRel>()
                .in(WeAutoTagUserRel::getRuleId, hadUserScopeRuleIdList)
                .eq(WeAutoTagUserRel::getTargetId, userId)
                .eq(WeAutoTagUserRel::getType, WeConstans.AUTO_TAG_ADD_USER_TYPE)
                );
        //查询部门下的员工
        List<WeAutoTagUserRel> listFromDepartment = weAutoTagUserRelService.getInfoByUserIdFromDepartment(corpId, userId,hadUserScopeRuleIdList);
        if(CollectionUtils.isNotEmpty(listFromDepartment)){
            list.addAll(listFromDepartment);
        }
        Set<Long> ruleIdSet = list.stream().map(WeAutoTagUserRel::getRuleId).collect(Collectors.toSet());

        return this.list(new LambdaQueryWrapper<WeAutoTagKeyword>()
                .in(WeAutoTagKeyword::getRuleId, ruleIdSet)).stream().collect(Collectors.groupingBy(WeAutoTagKeyword::getRuleId));
    }
}

