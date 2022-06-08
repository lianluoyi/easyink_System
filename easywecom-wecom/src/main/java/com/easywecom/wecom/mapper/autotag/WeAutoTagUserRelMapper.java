package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;
import com.easywecom.wecom.domain.vo.autotag.TagRuleUserInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标签与员工使用范围表(WeAutoTagUserRel)表数据库访问层
 *
 * @author tigger
 * @since 2022-02-27 15:52:45
 */
@Repository
public interface WeAutoTagUserRelMapper extends BaseMapper<WeAutoTagUserRel> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagUserRel> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagUserRel> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagUserRel> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagUserRel> entities);


    /**
     * 查询员工详情列表通过规则id
     * select抓取接口
     *
     * @param ruleId
     * @param corpId
     * @return
     */
    List<TagRuleUserInfoVO> listUserListByRuleId(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
}

