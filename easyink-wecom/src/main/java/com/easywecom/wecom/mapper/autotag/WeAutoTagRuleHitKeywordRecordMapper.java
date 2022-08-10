package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecord;
import com.easywecom.wecom.domain.query.autotag.TagRuleRecordKeywordDetailQuery;
import com.easywecom.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easywecom.wecom.domain.vo.autotag.record.keyword.KeywordRecordDetailVO;
import com.easywecom.wecom.domain.vo.autotag.record.keyword.KeywordTagRuleRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户打标签记录表(WeAutoTagRuleHitKeywordRecord)表数据库访问层
 *
 * @author tigger
 * @since 2022-03-02 14:51:18
 */
public interface WeAutoTagRuleHitKeywordRecordMapper extends BaseMapper<WeAutoTagRuleHitKeywordRecord> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitKeywordRecord> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRuleHitKeywordRecord> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitKeywordRecord> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRuleHitKeywordRecord> entities);

    /**
     * 关键词记录列表
     *
     * @param query
     * @return
     */
    List<KeywordTagRuleRecordVO> listKeywordRecord(TagRuleRecordQuery query);

    /**
     * 触发关键词详情列表
     *
     * @param query
     * @return
     */
    List<KeywordRecordDetailVO> listKeywordDetail(TagRuleRecordKeywordDetailQuery query);

    /**
     * 客户统计
     *
     * @param ruleId 规则id
     * @param corpId 企业id
     * @return
     */
    List<String> keywordCustomerCount(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
}

