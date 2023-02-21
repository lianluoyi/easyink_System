package com.easyink.wecom.mapper.form;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.domain.ChangeSortModel;
import com.easyink.wecom.domain.entity.form.WeFormGroup;
import com.easyink.wecom.domain.enums.form.FormSourceType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单分组表(WeFormGroup)表数据库访问层
 *
 * @author tigger
 * @since 2023-01-09 11:23:21
 */
public interface WeFormGroupMapper extends BaseMapper<WeFormGroup> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormGroup> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeFormGroup> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormGroup> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeFormGroup> entities);

    /**
     * 查询对应条件下的表单分组列表
     *
     * @param corpId       企业id
     * @param sourceType   分组所属类别(1:企业 2: 部门 3:个人) {@link FormSourceType}
     * @param departmentId 部门id，当sourceType为2时，才需要
     * @param createBy     创建人
     * @return WeFormGroup
     */
    List<WeFormGroup> selectByCorpIdAndSourceType(@Param("corpId") String corpId, @Param("sourceType") Integer sourceType, @Param("departmentId") Integer departmentId, @Param("createBy") String createBy);

    /**
     * 查询名称相同的表单分组数量
     *
     * @param name   分组名称
     * @param corpId 企业id
     * @return
     */
    Integer countSameNameNum(@Param("name") String name, @Param("corpId") String corpId);

    /**
     * 修改当前级别分组的排序
     *
     * @param corpId   企业id
     * @param sortList 排序列表
     */
    void changeSort(@Param("corpId") String corpId, @Param("sortList") List<ChangeSortModel> sortList);

    /**
     * 查询子分组列表
     *
     * @param groupId 分组id
     * @param corpId  企业id
     * @return 子分组id列表
     */
    List<Integer> selectChildGroupIdList(@Param("groupId") Integer groupId, @Param("corpId") String corpId);

    /**
     * 查询分组及其子分组的id列表
     *
     * @param id 当前分组id
     * @return 分组及其子分组id列表
     */
    List<Integer> selectGroupIdListContainChildById(@Param("id") Integer id);

    /**
     * 删除分组
     * @param groupIdList 分组id列表
     */
    void deleteBatchByIdList(@Param("groupIdList") List<Integer> groupIdList);
}

