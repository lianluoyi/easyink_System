package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecordTagRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitGroupRecordTagRel)表数据库访问层
 *
 * @author tigger
 * @since 2022-03-02 15:42:28
 */
@Repository
public interface WeAutoTagRuleHitGroupRecordTagRelMapper extends BaseMapper<WeAutoTagRuleHitGroupRecordTagRel> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitGroupRecordTagRel> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRuleHitGroupRecordTagRel> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitGroupRecordTagRel> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRuleHitGroupRecordTagRel> entities);

    /**
     * 查询群记录去重标签列表
     *
     * @param ruleId     规则id
     * @param customerId 客户id
     * @param groupId    群id
     * @return
     */
    List<String> listDistinctTagNameList(@Param("ruleId") Long ruleId, @Param("customerId") String customerId, @Param("groupId") String groupId);
}

