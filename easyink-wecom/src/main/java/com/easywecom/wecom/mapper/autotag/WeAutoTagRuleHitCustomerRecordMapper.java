package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitCustomerRecord;
import com.easywecom.wecom.domain.query.autotag.CustomerTagRuleRecordQuery;
import com.easywecom.wecom.domain.vo.autotag.record.customer.CustomerTagRuleRecordVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户打标签记录表(WeAutoTagRuleHitCustomerRecord)表数据库访问层
 *
 * @author tigger
 * @since 2022-03-02 16:04:51
 */
@Repository
public interface WeAutoTagRuleHitCustomerRecordMapper extends BaseMapper<WeAutoTagRuleHitCustomerRecord> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitCustomerRecord> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRuleHitCustomerRecord> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitCustomerRecord> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRuleHitCustomerRecord> entities);

    /**
     * 新客规则记录列表
     *
     * @param query
     * @return
     */
    List<CustomerTagRuleRecordVO> listCustomerRecord(CustomerTagRuleRecordQuery query);

    /**
     * 新客客户统计
     *
     *
     * @param ruleId
     * @param corpId 企业id
     * @return
     */
    List<String> customerCustomerCount(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
}

