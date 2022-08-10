package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagGroupScene;
import com.easyink.wecom.domain.vo.autotag.group.GroupSceneVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群标签场景表(WeAutoTagGroupScene)表数据库访问层
 *
 * @author tigger
 * @since 2022-02-27 15:52:33
 */
@Repository
public interface WeAutoTagGroupSceneMapper extends BaseMapper<WeAutoTagGroupScene> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagGroupScene> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagGroupScene> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagGroupScene> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagGroupScene> entities);

    /**
     * 查询群场景列表通过规则id
     * select抓取接口
     *
     * @param ruleId
     * @param corpId
     * @return
     */
    List<GroupSceneVO> listGroupSceneListByRuleId(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
}

