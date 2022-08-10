package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.autotag.TagRuleBatchStatusDTO;
import com.easyink.wecom.domain.dto.autotag.TagRuleDeleteDTO;
import com.easyink.wecom.domain.dto.autotag.customer.AddCustomerTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.customer.UpdateCustomerTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.group.AddGroupTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.group.UpdateGroupTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.keyword.AddKeywordTagRuleDTO;
import com.easyink.wecom.domain.dto.autotag.keyword.UpdateKeywordTagRuleDTO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRule;
import com.easyink.wecom.domain.query.autotag.RuleInfoQuery;
import com.easyink.wecom.domain.query.autotag.TagRuleQuery;
import com.easyink.wecom.domain.vo.autotag.TagRuleListVO;
import com.easyink.wecom.domain.vo.autotag.customer.TagRuleCustomerInfoVO;
import com.easyink.wecom.domain.vo.autotag.group.TagRuleGroupInfoVO;
import com.easyink.wecom.domain.vo.autotag.keyword.TagRuleKeywordInfoVO;

import java.util.List;

/**
 * 标签规则表(WeAutoTagRule)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:40
 */
public interface WeAutoTagRuleService extends IService<WeAutoTagRule> {


    /**
     * 关键词规则列表
     *
     * @return
     */
    List<TagRuleListVO> listKeyword(TagRuleQuery query);

    /**
     * 群规则列表
     *
     * @return
     */
    List<TagRuleListVO> listGroup(TagRuleQuery query);

    /**
     * 新客规则列表
     *
     * @return
     */
    List<TagRuleListVO> listCustomer(TagRuleQuery query);


    /**
     * 关键词规则详情
     *
     * @param query
     * @return
     */
    TagRuleKeywordInfoVO keywordInfo(RuleInfoQuery query);

    /**
     * 群规则详情
     *
     * @param query
     * @return
     */
    TagRuleGroupInfoVO groupInfo(RuleInfoQuery query);

    /**
     * 新客规则详情
     *
     * @param query
     * @return
     */
    TagRuleCustomerInfoVO customerInfo(RuleInfoQuery query);

    /**
     * 新增关键词类型标签规则
     *
     * @param addKeywordTagRuleDTO
     */
    int keywordAdd(AddKeywordTagRuleDTO addKeywordTagRuleDTO);

    /**
     * 新增群类型标签规则
     *
     * @param addGroupTagRuleDTO
     * @return
     */
    int groupAdd(AddGroupTagRuleDTO addGroupTagRuleDTO);

    /**
     * 新增新客类型标签规则
     *
     * @param addCustomerTagRuleDTO
     * @return
     */
    int customerAdd(AddCustomerTagRuleDTO addCustomerTagRuleDTO);

    /**
     * 修改关键词类型标签规则
     *
     * @param updateKeywordTagRuleDTO
     * @param corpId
     * @return
     */
    int keywordEdit(UpdateKeywordTagRuleDTO updateKeywordTagRuleDTO, String corpId);

    /**
     * 修改群类型标签规则
     *
     * @param updateGroupTagRuleDTO
     * @param corpId
     * @return
     */
    int groupEdit(UpdateGroupTagRuleDTO updateGroupTagRuleDTO, String corpId);

    /**
     * 修改新客类型标签规则
     *
     * @param updateCustomerTagRuleDTO
     * @param corpId
     * @return
     */
    int customerEdit(UpdateCustomerTagRuleDTO updateCustomerTagRuleDTO, String corpId);

    /**
     * 删除关键词标签规则
     *
     * @param deleteDTO
     * @param corpId
     * @return
     */
    int keywordDelete(TagRuleDeleteDTO deleteDTO, String corpId);

    /**
     * 删除群标签规则
     *
     * @param deleteDTO
     * @param corpId
     * @return
     */
    int groupDelete(TagRuleDeleteDTO deleteDTO, String corpId);

    /**
     * 删除新客标签规则
     *
     * @param deleteDTO
     * @param corpId
     * @return
     */
    int customerDelete(TagRuleDeleteDTO deleteDTO, String corpId);

    /**
     * 批量启用禁用
     *
     * @param tagRuleBatchStatusDTO
     * @return
     */
    Boolean batchStatus(TagRuleBatchStatusDTO tagRuleBatchStatusDTO);

    /**
     * 获取可用的新客标签规则
     *
     * @param corpId
     * @return
     */
    List<Long> getCandidateCustomerRuleIdList(String corpId);

    /**
     * 查询包含员工适用范围的规则id列表
     *
     * @param corpId
     * @param labelType
     * @return
     */
    List<Long> listContainUserScopeRuleIdList(String corpId, Integer labelType);

    /**
     * 根据标签类型查询规则
     *
     * @param labelType 标签类型
     * @param corpId    企业id
     * @return
     */
    List<Long> selectEnableRuleIdByLabelType(Integer labelType, String corpId);


    /**
     * 根据标签类型查询规则列表
     *
     * @param labelType 标签类型
     * @param corpId    企业id
     * @return
     */
    List<Long> listRuleIdByLabelType(Integer labelType, String corpId);
}

