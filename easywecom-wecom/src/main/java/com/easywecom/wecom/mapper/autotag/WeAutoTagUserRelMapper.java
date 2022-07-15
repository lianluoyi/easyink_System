package com.easywecom.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;
import com.easywecom.wecom.domain.vo.autotag.TagRuleDepartmentInfoVO;
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

    /**
     * 通过规则id查询部门信息
     *
     * @param ruleId
     * @param corpId
     * @return
     */
    List<TagRuleDepartmentInfoVO> listDepartmentListByRuleId(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
    /**
     * 查询员工是否在部门内，如果在则返回符合条件的数据
     *
     * @param userId
     * @param hadUserScopeRuleIdList
     * @return
     */
    List<WeAutoTagUserRel> listWeAutoTagUserRelByUserIdFromDepartment(@Param("cropId") String cropId, @Param("userId") String userId, @Param("list") List<Long> hadUserScopeRuleIdList);

    /**
     * 返回接收消息的员工含有自动标签规则的规则id
     *
     * @param cropId
     * @param userId
     * @param hadUserScopeRuleIdList
     * @return
     */
    List<Long> listWeAutoTagRelByUserIdAndRuleIdList(@Param("cropId") String cropId, @Param("userId") String userId, @Param("list") List<Long> hadUserScopeRuleIdList);
}

