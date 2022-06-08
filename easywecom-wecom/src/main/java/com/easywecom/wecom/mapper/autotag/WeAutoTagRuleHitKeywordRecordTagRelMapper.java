package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecordTagRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitKeywordRecordTagRel)表数据库访问层
 *
 * @author tigger
 * @since 2022-03-02 14:51:28
 */
@Repository
public interface WeAutoTagRuleHitKeywordRecordTagRelMapper extends BaseMapper<WeAutoTagRuleHitKeywordRecordTagRel> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitKeywordRecordTagRel> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRuleHitKeywordRecordTagRel> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitKeywordRecordTagRel> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRuleHitKeywordRecordTagRel> entities);

    /**
     * 查询去重后的标签名称列表
     *  SELECT查询
     * @return
     */
    List<String> listDistinctTagNameList(@Param("customerId") String customerId, @Param("userId") String userId,
                                         @Param("ruleId") Long ruleId);

}

