package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.autotag.AutoTagLabelTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.autotag.TagRuleBatchStatusDTO;
import com.easyink.wecom.domain.dto.autotag.TagRuleDeleteDTO;
import com.easyink.wecom.domain.dto.autotag.customer.AddCustomerTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.customer.UpdateCustomerTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.group.AddGroupTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.group.UpdateGroupTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.keyword.AddKeywordTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.keyword.UpdateKeywordTagRuleDTO;
import com.easyink.wecom.domain.entity.autotag.*;
import com.easyink.wecom.domain.query.autotag.RuleInfoQuery;
import com.easyink.wecom.domain.query.autotag.TagRuleQuery;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import com.easyink.wecom.domain.vo.autotag.TagRuleListVO;
import com.easyink.wecom.domain.vo.autotag.customer.TagRuleCustomerInfoVO;
import com.easyink.wecom.domain.vo.autotag.group.TagRuleGroupInfoVO;
import com.easyink.wecom.domain.vo.autotag.keyword.TagRuleKeywordInfoVO;
import com.easyink.wecom.mapper.autotag.WeAutoTagRuleMapper;
import com.easyink.wecom.service.autotag.*;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签规则表(WeAutoTagRule)表服务实现类
 *
 * @author tigger
 * @since 2022-02-27 15:52:40
 */
@Service("weAutoTagRuleService")
public class WeAutoTagRuleServiceImpl extends ServiceImpl<WeAutoTagRuleMapper, WeAutoTagRule> implements WeAutoTagRuleService {

    @Autowired
    private WeAutoTagRuleMapper weAutoTagRuleMapper;
    @Autowired
    private WeAutoTagUserRelService weAutoTagUserRelService;
    @Autowired
    private WeAutoTagKeywordService weAutoTagKeywordService;
    @Autowired
    private WeAutoTagKeywordTagRelService weAutoTagKeywordTagRelService;
    @Autowired
    private WeAutoTagGroupSceneService weAutoTagGroupSceneService;
    @Autowired
    private WeAutoTagGroupSceneGroupRelService weAutoTagGroupSceneGroupRelService;
    @Autowired
    private WeAutoTagGroupSceneTagRelService weAutoTagGroupSceneTagRelService;
    @Autowired
    private WeAutoTagCustomerRuleEffectTimeService weAutoTagCustomerRuleEffectTimeService;
    @Autowired
    private WeAutoTagCustomerSceneService weAutoTagCustomerSceneService;
    @Autowired
    private WeAutoTagCustomerSceneTagRelService weAutoTagCustomerSceneTagRelService;

    /**
     * 关键词规则列表
     *
     * @param query
     * @return
     */
    @Override
    @DataScope
    public List<TagRuleListVO> listKeyword(TagRuleQuery query) {
        checkCorpId(query.getCorpId());
        return filterTag(weAutoTagRuleMapper.listKeyword(query), query.getTagIdList());
    }


    /**
     * 群规则列表
     *
     * @param query
     * @return
     */
    @Override
    @DataScope
    public List<TagRuleListVO> listGroup(TagRuleQuery query) {
        return filterTag(weAutoTagRuleMapper.listGroup(query), query.getTagIdList());
    }

    /**
     * 新客规则列表
     *
     * @param query
     * @return
     */
    @Override
    @DataScope
    public List<TagRuleListVO> listCustomer(TagRuleQuery query) {
        return filterTag(weAutoTagRuleMapper.listCustomer(query), query.getTagIdList());
    }

    /**
     * 关键词规则详情
     *
     * @return
     */
    @Override
    public TagRuleKeywordInfoVO keywordInfo(RuleInfoQuery query) {
        checkCorpId(query.getCorpId());
        return weAutoTagRuleMapper.keywordInfo(query);
    }

    /**
     * 群规则详情
     *
     * @return
     */
    @Override
    public TagRuleGroupInfoVO groupInfo(RuleInfoQuery query) {
        checkCorpId(query.getCorpId());
        return weAutoTagRuleMapper.groupInfo(query);
    }

    /**
     * 新客规则详情
     *
     * @return
     */
    @Override
    public TagRuleCustomerInfoVO customerInfo(RuleInfoQuery query) {
        checkCorpId(query.getCorpId());
        return weAutoTagRuleMapper.customerInfo(query);
    }

    /**
     * 新增关键词类型标签规则
     *
     * @param addKeywordTagRuleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int keywordAdd(AddKeywordTagRuleDTO addKeywordTagRuleDTO) {
        StringUtils.checkCorpId(addKeywordTagRuleDTO.getCorpId());

        // 添加 规则
        WeAutoTagRule weAutoTagRule = addKeywordTagRuleDTO.toWeAutoTagRule();

        int result = weAutoTagRuleMapper.insert(weAutoTagRule);
        final Long ruleId = weAutoTagRule.getId();
        // 添加 员工/部门关系
        weAutoTagUserRelService.batchSave(addKeywordTagRuleDTO.toWeAutoTagUserRel(ruleId));
        // 添加关键词
        weAutoTagKeywordService.batchSave(addKeywordTagRuleDTO.toWeAutoTagKeywordList(ruleId));
        // 添加标签
        weAutoTagKeywordTagRelService.batchSave(addKeywordTagRuleDTO.toWeAutoTagKeywordTagRelList(ruleId));

        return result;
    }

    /**
     * 新增群类型标签规则
     *
     * @param addGroupTagRuleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int groupAdd(AddGroupTagRuleDTO addGroupTagRuleDTO) {
        StringUtils.checkCorpId(addGroupTagRuleDTO.getCorpId());
        // 添加 规则
        WeAutoTagRule weAutoTagRule = addGroupTagRuleDTO.toWeAutoTagRule();
        int result = weAutoTagRuleMapper.insert(weAutoTagRule);
        final Long ruleId = weAutoTagRule.getId();

        // 添加 群场景
        List<WeAutoTagGroupScene> weAutoTagGroupSceneList = addGroupTagRuleDTO.toWeAutoTagGroupSceneList(ruleId, addGroupTagRuleDTO.getCorpId());
        weAutoTagGroupSceneService.batchSave(weAutoTagGroupSceneList);
        // 添加 群关系
        List<Long> groupSceneIdList = weAutoTagGroupSceneList.stream().map(WeAutoTagGroupScene::getId).collect(Collectors.toList());
        weAutoTagGroupSceneGroupRelService.batchSave(addGroupTagRuleDTO.toWeAutoTagGroupSceneGroupRelList(groupSceneIdList, ruleId));
        // 添加 标签关系
        weAutoTagGroupSceneTagRelService.batchSave(addGroupTagRuleDTO.toWeAutoTagGroupSceneTagRelList(groupSceneIdList, ruleId));

        return result;
    }

    /**
     * 新增新客类型标签规则
     *
     * @param addCustomerTagRuleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int customerAdd(AddCustomerTagRuleDTO addCustomerTagRuleDTO) {
        StringUtils.checkCorpId(addCustomerTagRuleDTO.getCorpId());
        // 添加 规则
        WeAutoTagRule weAutoTagRule = addCustomerTagRuleDTO.toWeAutoTagRule();
        int result = weAutoTagRuleMapper.insert(weAutoTagRule);
        final Long ruleId = weAutoTagRule.getId();

        // 添加规则生效时间
        WeAutoTagCustomerRuleEffectTime weAutoTagCustomerRuleEffectTime = addCustomerTagRuleDTO.toWeAutoTagCustomerRuleEffectTime(ruleId);
        if (weAutoTagCustomerRuleEffectTime != null) {
            weAutoTagCustomerRuleEffectTimeService.save(weAutoTagCustomerRuleEffectTime);
        }
        if (CollectionUtils.isEmpty(addCustomerTagRuleDTO.getUserIdList()) && CollectionUtils.isEmpty(addCustomerTagRuleDTO.getDepartmentIdList())) {
            // 员工、部门都为空，表示该规则为全部员工范围 type = 3
            weAutoTagUserRelService.save(new WeAutoTagUserRel(ruleId, StringUtils.EMPTY, WeConstans.AUTO_TAG_ADD_ALL_USER));
        } else {
            // 添加 员工关系
            weAutoTagUserRelService.batchSave(addCustomerTagRuleDTO.toWeAutoTagUserRel(ruleId));
        }

        // 添加 新客场景
        List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList = addCustomerTagRuleDTO.toWeAutoTagCustomerSceneList(ruleId, addCustomerTagRuleDTO.getCorpId());
        weAutoTagCustomerSceneService.batchSave(weAutoTagCustomerSceneList);

        // 添加场景标签
        List<Long> customerSceneIdList = weAutoTagCustomerSceneList.stream().map(WeAutoTagCustomerScene::getId).collect(Collectors.toList());
        weAutoTagCustomerSceneTagRelService.batchSave(addCustomerTagRuleDTO.toWeAutoTagCustomerSceneTagRelList(customerSceneIdList, ruleId));

        return result;
    }

    /**
     * 修改关键词类型标签规则
     *
     * @param updateKeywordTagRuleDTO
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int keywordEdit(UpdateKeywordTagRuleDTO updateKeywordTagRuleDTO, String corpId) {
        checkCorpId(corpId);
        // 修改规则
        WeAutoTagRule weAutoTagRule = updateKeywordTagRuleDTO.toWeAutoTagRule();
        int result = weAutoTagRuleMapper.updateById(weAutoTagRule);
        Long ruleId = weAutoTagRule.getId();
        // 修改员工使用范围
        weAutoTagUserRelService.edit(updateKeywordTagRuleDTO.toWeAutoTagUserRel(ruleId), ruleId);
        // 修改关键词
        weAutoTagKeywordService.edit(updateKeywordTagRuleDTO.getReomveFuzzyMatchKeywordList(),
                updateKeywordTagRuleDTO.getRemoveExactMatchKeywordList(), updateKeywordTagRuleDTO.toWeAutoTagKeywordList(), ruleId);
        // 修改关键词标签
        weAutoTagKeywordTagRelService.edit(updateKeywordTagRuleDTO.getRemoveTagIdList(),
                updateKeywordTagRuleDTO.toWeAutoTagKeywordTagRelList(), ruleId);
        return result;
    }

    /**
     * 修改群类型标签规则
     *
     * @param updateGroupTagRuleDTO
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int groupEdit(UpdateGroupTagRuleDTO updateGroupTagRuleDTO, String corpId) {
        checkCorpId(corpId);
        WeAutoTagRule weAutoTagRule = updateGroupTagRuleDTO.toWeAutoTagRule();
        int result = weAutoTagRuleMapper.updateById(weAutoTagRule);
        Long ruleId = weAutoTagRule.getId();

        // 修改 群场景
        List<WeAutoTagGroupScene> weAutoTagGroupSceneList = updateGroupTagRuleDTO.toWeAutoTagGroupSceneList(ruleId, corpId);
        weAutoTagGroupSceneService.edit(updateGroupTagRuleDTO.getRemoveSceneIdList(), weAutoTagGroupSceneList);
        // 修改 群场景群关系
        List<Long> groupSceneIdList = weAutoTagGroupSceneList.stream().map(WeAutoTagGroupScene::getId).collect(Collectors.toList());
        weAutoTagGroupSceneGroupRelService.edit(updateGroupTagRuleDTO.getGroupSceneList(), updateGroupTagRuleDTO.toWeAutoTagGroupSceneGroupRelList(groupSceneIdList, ruleId));
        // 修改 群场景标签关系
        weAutoTagGroupSceneTagRelService.edit(updateGroupTagRuleDTO.getGroupSceneList(), updateGroupTagRuleDTO.toWeAutoTagGroupSceneTagRelList(groupSceneIdList, ruleId));

        return result;
    }

    /**
     * 修改新客类型标签规则
     *
     * @param updateCustomerTagRuleDTO
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int customerEdit(UpdateCustomerTagRuleDTO updateCustomerTagRuleDTO, String corpId) {
        checkCorpId(corpId);
        WeAutoTagRule weAutoTagRule = updateCustomerTagRuleDTO.toWeAutoTagRule();
        int result = weAutoTagRuleMapper.updateById(weAutoTagRule);
        Long ruleId = weAutoTagRule.getId();
        // 编辑时先把原来的删除
        weAutoTagUserRelService.remove(new LambdaQueryWrapper<WeAutoTagUserRel>().eq(WeAutoTagUserRel::getRuleId, ruleId));
        if (CollectionUtils.isEmpty(updateCustomerTagRuleDTO.getUserIdList()) && CollectionUtils.isEmpty(updateCustomerTagRuleDTO.getDepartmentIdList())) {
            // 员工、部门都为空，表示该规则为全部员工范围 type = 3
            weAutoTagUserRelService.save(new WeAutoTagUserRel(ruleId, StringUtils.EMPTY, WeConstans.AUTO_TAG_ADD_ALL_USER));
        } else {
            // 添加 员工关系
            weAutoTagUserRelService.batchSave(updateCustomerTagRuleDTO.toWeAutoTagUserRel(ruleId));
        }
        // 修改 生效时间
        WeAutoTagCustomerRuleEffectTime weAutoTagCustomerRuleEffectTime = updateCustomerTagRuleDTO.toWeAutoTagCustomerRuleEffectTime(ruleId);
        // 不等于null->修改
        if (weAutoTagCustomerRuleEffectTime != null) {
            weAutoTagCustomerRuleEffectTimeService.edit(weAutoTagCustomerRuleEffectTime);
        }else{
            // 等于null -> 删除
            weAutoTagCustomerRuleEffectTimeService.removeByRuleIdList(Collections.singletonList(ruleId));
        }
        // 修改 客户场景
        List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList = updateCustomerTagRuleDTO.toWeAutoTagCustomerSceneList(ruleId, corpId);
        weAutoTagCustomerSceneService.edit(updateCustomerTagRuleDTO.getRemoveSceneIdList(), weAutoTagCustomerSceneList);

        // 修改 客户场景标签
        List<Long> customerSceneIdList = weAutoTagCustomerSceneList.stream().map(WeAutoTagCustomerScene::getId).collect(Collectors.toList());
        weAutoTagCustomerSceneTagRelService.edit(updateCustomerTagRuleDTO.getCustomerSceneList(), updateCustomerTagRuleDTO.toWeAutoTagCustomerSceneTagRelList(customerSceneIdList, ruleId));

        return result;
    }

    /**
     * 删除关键词标签规则
     *
     * @param deleteDTO
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int keywordDelete(TagRuleDeleteDTO deleteDTO, String corpId) {
        checkDeleteParam(deleteDTO, corpId);
        List<Long> removeRuleIdList = deleteDTO.getIdList();
        // 删除规则
        int result = this.baseMapper.deleteBatchIds(removeRuleIdList);

        // 删除使用员工范围
        weAutoTagUserRelService.removeByRuleIdList(removeRuleIdList);

        // 删除关键词
        weAutoTagKeywordService.removeByRuleIdList(removeRuleIdList);
        // 删除标签
        weAutoTagKeywordTagRelService.removeByRuleIdList(removeRuleIdList);

        return result;
    }


    /**
     * 删除群标签规则
     *
     * @param deleteDTO
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int groupDelete(TagRuleDeleteDTO deleteDTO, String corpId) {
        checkDeleteParam(deleteDTO, corpId);
        List<Long> removeRuleIdList = deleteDTO.getIdList();
        // 删除规则
        int result = this.baseMapper.deleteBatchIds(removeRuleIdList);
        // 删除场景
        weAutoTagGroupSceneService.removeByRuleIdList(removeRuleIdList);
        // 删除群信息
        weAutoTagGroupSceneGroupRelService.removeByRuleIdList(removeRuleIdList);
        // 删除标签信息
        weAutoTagGroupSceneTagRelService.removeRuleIdList(removeRuleIdList);
        return result;
    }

    /**
     * 删除新客标签规则
     *
     * @param deleteDTO
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int customerDelete(TagRuleDeleteDTO deleteDTO, String corpId) {
        checkDeleteParam(deleteDTO, corpId);
        List<Long> removeRuleIdList = deleteDTO.getIdList();
        // 删除规则
        int result = this.baseMapper.deleteBatchIds(removeRuleIdList);
        // 删除使用人员范围
        weAutoTagUserRelService.removeByRuleIdList(removeRuleIdList);
        // 删除生效时间
        weAutoTagCustomerRuleEffectTimeService.removeByRuleIdList(removeRuleIdList);
        // 删除场景
        weAutoTagCustomerSceneService.removeByRuleIdList(removeRuleIdList);
        // 删除标签
        weAutoTagCustomerSceneTagRelService.removeByRuleIdList(removeRuleIdList);
        return result;
    }

    /**
     * 批量启用禁用
     *
     * @param tagRuleBatchStatusDTO
     * @return
     */
    @Override
    public Boolean batchStatus(TagRuleBatchStatusDTO tagRuleBatchStatusDTO) {
        StringUtils.checkCorpId(tagRuleBatchStatusDTO.getCorpId());
        if (tagRuleBatchStatusDTO == null || tagRuleBatchStatusDTO.getStatus() == null
                || CollectionUtils.isEmpty(tagRuleBatchStatusDTO.getIdList())) {
            log.error("参数异常");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.update(new LambdaUpdateWrapper<WeAutoTagRule>()
                .set(WeAutoTagRule::getStatus, tagRuleBatchStatusDTO.getStatus())
                .in(WeAutoTagRule::getId, tagRuleBatchStatusDTO.getIdList()));
    }

    /**
     * 获取可用的新客标签规则
     *
     * @param corpId
     * @return
     */
    @Override
    public List<Long> getCandidateCustomerRuleIdList(String corpId) {
        StringUtils.checkCorpId(corpId);
        List<Long> candidatesRuleIdList = new ArrayList<>();
        // 获取启用中的所有新客规则
        List<Long> normalRuleIdList = this.selectEnableRuleIdByLabelType(AutoTagLabelTypeEnum.CUSTOMER.getType(), corpId);
        if (CollectionUtils.isEmpty(normalRuleIdList)) {
            return candidatesRuleIdList;
        }

        // 查询不存在生效时间得新客标签规则,直接添加到候选id列表
        List<Long> notHadEffectTimeRuleIdList = new ArrayList<>(normalRuleIdList);
        List<Long> hasEffectTimeRuleIdList = weAutoTagCustomerRuleEffectTimeService.selectHadEffectTimeRule(normalRuleIdList, corpId);
        if (CollectionUtils.isNotEmpty(hasEffectTimeRuleIdList)) {
            // 获取存在生效时间的规则id列表
            notHadEffectTimeRuleIdList.removeAll(hasEffectTimeRuleIdList);
        }
        candidatesRuleIdList.addAll(notHadEffectTimeRuleIdList);

        // 从hasEffectTimeRuleIdList中获取有效的规则id,并添加到候选id列表
        if (CollectionUtils.isNotEmpty(hasEffectTimeRuleIdList)) {
            String nowStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date());
            List<WeAutoTagCustomerRuleEffectTime> list = weAutoTagCustomerRuleEffectTimeService.list(new LambdaQueryWrapper<WeAutoTagCustomerRuleEffectTime>()
                    .le(WeAutoTagCustomerRuleEffectTime::getEffectBeginTime, nowStr)
                    .ge(WeAutoTagCustomerRuleEffectTime::getEffectEndTime, nowStr)
                    .in(WeAutoTagCustomerRuleEffectTime::getRuleId, hasEffectTimeRuleIdList)
                    .select(WeAutoTagCustomerRuleEffectTime::getRuleId));
            // 添加有效时间内的规则id列表
            candidatesRuleIdList.addAll(list.stream().map(WeAutoTagCustomerRuleEffectTime::getRuleId).collect(Collectors.toList()));
        }

        return candidatesRuleIdList;
    }


    /**
     * 查询包含员工适用范围的规则id列表
     *
     * @param corpId
     * @param labelType
     * @return
     */
    @Override
    public List<Long> listContainUserScopeRuleIdList(String corpId, Integer labelType) {
        if (labelType.equals(AutoTagLabelTypeEnum.GROUP.getType()) || StringUtils.isBlank(corpId)) {
            return Lists.newArrayList();
        }
        return this.baseMapper.selectContainUserScopeRuleIdList(corpId, labelType);
    }

    /**
     * 根据标签类型查询规则
     *
     * @param labelType 标签类型
     * @param corpId    企业id
     * @return
     */
    @Override
    public List<Long> selectEnableRuleIdByLabelType(Integer labelType, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (!AutoTagLabelTypeEnum.existType(labelType)) {
            log.error("labelType error:  {}");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.baseMapper.selectList(new LambdaQueryWrapper<WeAutoTagRule>()
                .eq(WeAutoTagRule::getLabelType, labelType)
                .eq(WeAutoTagRule::getStatus, 1)
                .eq(WeAutoTagRule::getCorpId, corpId))
                .stream()
                .map(WeAutoTagRule::getId).collect(Collectors.toList());
    }

    /**
     * 根据标签类型查询规则列表
     *
     * @param labelType 标签类型
     * @param corpId    企业id
     * @return
     */
    @Override
    public List<Long> listRuleIdByLabelType(Integer labelType, String corpId) {
        if (!AutoTagLabelTypeEnum.existType(labelType) || StringUtils.isBlank(corpId)) {
            return Lists.newArrayList();
        }
        return this.baseMapper.selectRuleIdByLabelType(labelType, corpId);
    }

    /**
     * 校验删除操作参数
     *
     * @param deleteDTO
     * @param corpId
     */
    private void checkDeleteParam(TagRuleDeleteDTO deleteDTO, String corpId) {
        checkCorpId(corpId);
        if (deleteDTO == null || CollectionUtils.isEmpty(deleteDTO.getIdList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    /**
     * 校验企业id
     *
     * @param corpId 企业id
     */
    private void checkCorpId(String corpId) {
        StringUtils.checkCorpId(corpId);
    }

    /**
     * 过滤标签
     *
     * @param list      过滤原始列表
     * @param tagIdList 过滤的标签idlist
     */
    private List<TagRuleListVO> filterTag(List<TagRuleListVO> list, List<String> tagIdList) {
        if (CollectionUtils.isNotEmpty(tagIdList)) {
            return list.stream().filter(item -> {
                List<String> originTagIdList = item.getTagList().stream().map(TagInfoVO::getTagId).collect(Collectors.toList());
                return !Collections.disjoint(originTagIdList, tagIdList);
            }).collect(Collectors.toList());
        }
        return list;
    }

}

