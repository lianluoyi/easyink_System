package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeyword;

import java.util.List;
import java.util.Map;

/**
 * 关键词规则表(WeAutoTagKeyword)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:37
 */
public interface WeAutoTagKeywordService extends IService<WeAutoTagKeyword> {

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordList
     * @return
     */
    int batchSave(List<WeAutoTagKeyword> toWeAutoTagKeywordList);

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordList
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagKeyword> toWeAutoTagKeywordList, Boolean insertOnly);

    /**
     * 修改关键词
     *
     * @param reomveFuzzyMatchKeywordList
     * @param removeExactMatchKeywordList
     * @param toWeAutoTagKeywordList
     * @param ruleId
     * @return
     */
    int edit(List<String> reomveFuzzyMatchKeywordList, List<String> removeExactMatchKeywordList, List<WeAutoTagKeyword> toWeAutoTagKeywordList, Long ruleId);

    /**
     * 删除模糊匹配关键字
     *
     * @param reomveFuzzyMatchKeywordList
     * @param ruleId
     * @return
     */
    int removeFuzzyKeywordByRuleIdAndKeywordList(List<String> reomveFuzzyMatchKeywordList, Long ruleId);

    /**
     * 删除精确匹配关键字
     *
     * @param reomveFuzzyMatchKeywordList
     * @param ruleId
     * @return
     */
    int removeExactKeywordByRuleIdAndKeywordList(List<String> reomveFuzzyMatchKeywordList, Long ruleId);

    /**
     * 根据规则id列表删除关键词
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);

    /**
     * 根据规则id分组
     *
     * @param notHadUserScopeRuleIdList
     * @return
     */
    Map<Long, List<WeAutoTagKeyword>> listKeywordGroupByRuleIdInRuleIdList(List<Long> notHadUserScopeRuleIdList);

    /**
     * 从规则集合中查询关键词规则并根据规则id分组
     *
     * @param userId                 员工id
     * @param hadUserScopeRuleIdList 规则集合
     * @return
     */
    Map<Long, List<WeAutoTagKeyword>> listKeywordGroupByRuleIdByUserId(String corpId, String userId, List<Long> hadUserScopeRuleIdList);
}

