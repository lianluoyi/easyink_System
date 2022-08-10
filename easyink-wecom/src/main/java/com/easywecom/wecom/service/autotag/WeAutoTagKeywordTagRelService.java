package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagKeywordTagRel;

import java.util.List;
import java.util.Set;

/**
 * 关键词与标签关系表(WeAutoTagKeywordTagRel)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:39
 */
public interface WeAutoTagKeywordTagRelService extends IService<WeAutoTagKeywordTagRel> {

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordTagRelList
     * @return
     */
    int batchSave(List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList);

    /**
     * 批量添加
     *
     * @param toWeAutoTagKeywordTagRelList
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList, Boolean insertOnly);

    /**
     * 修改
     *
     * @param removeTagIdList
     * @param toWeAutoTagKeywordTagRelList
     * @param ruleId
     * @return
     */
    int edit(List<String> removeTagIdList, List<WeAutoTagKeywordTagRel> toWeAutoTagKeywordTagRelList, Long ruleId);

    /**
     * 删除指定规则id下的标签列表
     *
     * @param removeTagIdList
     * @param ruleId
     * @return
     */
    int removeBatchByRuleIdAndTagIdList(List<String> removeTagIdList, Long ruleId);

    /**
     * 根据规则id列表删除关键词语标签
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);

    /**
     * 根据规则id列表获取标签列表
     *
     * @param matchedRuleIdSet
     * @return
     */
    List<WeTag> getTagListByRuleIdList(Set<Long> matchedRuleIdSet);

}

