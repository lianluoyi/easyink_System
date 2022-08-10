package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitCustomerRecordTagRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitCustomerRecordTagRel)表数据库访问层
 *
 * @author tigger
 * @since 2022-03-02 16:04:53
 */
@Repository
public interface WeAutoTagRuleHitCustomerRecordTagRelMapper extends BaseMapper<WeAutoTagRuleHitCustomerRecordTagRel> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitCustomerRecordTagRel> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRuleHitCustomerRecordTagRel> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitCustomerRecordTagRel> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRuleHitCustomerRecordTagRel> entities);

    /**
     * 查询新客记录去重标签列表
     *
     * @param customerId 客户id
     * @param userId     员工id
     * @param ruleId     规则id
     * @return
     */
    List<String> listDistinctTagNameList(@Param("customerId") String customerId, @Param("userId") String userId,
                                         @Param("ruleId") Long ruleId);

}

