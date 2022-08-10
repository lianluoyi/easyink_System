package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecord;
import com.easywecom.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easywecom.wecom.domain.vo.autotag.record.group.GroupTagRuleRecordVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户打标签记录表(WeAutoTagRuleHitGroupRecord)表数据库访问层
 *
 * @author tigger
 * @since 2022-03-02 15:42:26
 */
@Repository
public interface WeAutoTagRuleHitGroupRecordMapper extends BaseMapper<WeAutoTagRuleHitGroupRecord> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitGroupRecord> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagRuleHitGroupRecord> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagRuleHitGroupRecord> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagRuleHitGroupRecord> entities);

    /**
     * 群记录列表
     *
     * @param query
     * @return
     */
    List<GroupTagRuleRecordVO> listGroupRecord(TagRuleRecordQuery query);

    /**
     * 群客户统计
     *
     *
     * @param ruleId
     * @param corpId
     * @return
     */
    List<String> groupCustomerCount(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
}

