package com.easyink.wecom.mapper.form;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单设置表(WeFormAdvanceSetting)表数据库访问层
 *
 * @author tigger
 * @since 2023-01-09 15:00:47
 */
public interface WeFormAdvanceSettingMapper extends BaseMapper<WeFormAdvanceSetting> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormAdvanceSetting> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeFormAdvanceSetting> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormAdvanceSetting> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeFormAdvanceSetting> entities);

}

