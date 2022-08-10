package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagCustomerScene;
import com.easywecom.wecom.domain.vo.autotag.customer.CustomerSceneVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 新客标签场景表(WeAutoTagCustomerScene)表数据库访问层
 *
 * @author tigger
 * @since 2022-02-27 15:52:30
 */
@Repository
public interface WeAutoTagCustomerSceneMapper extends BaseMapper<WeAutoTagCustomerScene> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagCustomerScene> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagCustomerScene> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagCustomerScene> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagCustomerScene> entities);

    /**
     * 查询新客场景列表根据规则id
     *
     * @param ruleId
     * @param corpId
     * @return
     */
    List<CustomerSceneVO> listCustomerSceneListByRuleId(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
}

