package com.easyink.wecom.mapper.form;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.form.WeFormUseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单使用记录表(WeFormUseRecord)表数据库访问层
 *
 * @author tigger
 * @since 2023-01-13 10:12:30
 */
public interface WeFormUseRecordMapper extends BaseMapper<WeFormUseRecord> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormUseRecord> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeFormUseRecord> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormUseRecord> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeFormUseRecord> entities);

    /**
     * 获取最新的五条使用记录的表单id列表
     * @param userId 员工id
     * @param corpId 企业id
     * @return 表单id列表
     */
    List<Integer> selectLimit5UseRecordFormIdList(@Param("userId") String userId, @Param("corpId") String corpId);
}

