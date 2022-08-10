package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRule;
import com.easywecom.wecom.domain.query.autotag.RuleInfoQuery;
import com.easywecom.wecom.domain.query.autotag.TagRuleQuery;
import com.easywecom.wecom.domain.vo.autotag.TagRuleListVO;
import com.easywecom.wecom.domain.vo.autotag.customer.TagRuleCustomerInfoVO;
import com.easywecom.wecom.domain.vo.autotag.group.TagRuleGroupInfoVO;
import com.easywecom.wecom.domain.vo.autotag.keyword.TagRuleKeywordInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标签规则表(WeAutoTagRule)表数据库访问层
 *
 * @author tigger
 * @since 2022-02-27 15:52:40
 */
@Repository
public interface WeAutoTagRuleMapper extends BaseMapper<WeAutoTagRule> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRule> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRule> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRule> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRule> entities);

    /**
     * 关键词规则列表
     *
     * @param query
     * @return
     */
    List<TagRuleListVO> listKeyword(TagRuleQuery query);

    /**
     * 群规则列表
     *
     * @param query
     * @return
     */
    List<TagRuleListVO> listGroup(TagRuleQuery query);

    /**
     * 新客规则列表
     *
     * @param query
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
     * 查询包含员工适用范围的规则id列表
     *
     * @param corpId
     * @param labelType
     * @return
     */
    List<Long> selectContainUserScopeRuleIdList(@Param("corpId") String corpId, @Param("labelType") Integer labelType);

    /**
     * 根据标签类型查询规则列表
     *
     * @param labelType 标签类型
     * @param corpId    企业id
     * @return
     */
    List<Long> selectRuleIdByLabelType(@Param("labelType") Integer labelType, @Param("corpId") String corpId);
}

