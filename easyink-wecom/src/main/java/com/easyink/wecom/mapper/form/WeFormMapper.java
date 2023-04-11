package com.easyink.wecom.mapper.form;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormMaterial;
import com.easyink.wecom.domain.query.form.FormQuery;
import com.easyink.wecom.domain.vo.form.FormDetailViewVO;
import com.easyink.wecom.domain.vo.form.FormPageVO;
import com.easyink.wecom.domain.vo.form.FormTotalView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 表单表(WeForm)表数据库访问层
 *
 * @author tigger
 * @since 2023-01-09 15:00:44
 */
@Mapper
public interface WeFormMapper extends BaseMapper<WeForm> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeForm> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeForm> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeForm> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeForm> entities);

    /**
     * 统计表单名称数量
     *
     * @param name   表单名称
     * @param corpId 企业id
     * @return 数量
     */
    Integer countSameNameNum(@Param("name") String name, @Param("corpId") String corpId);

    /**
     * 表单分页
     *
     * @param formQuery 查询query
     * @param corpId    企业id
     * @return 表单列表
     */
    List<FormPageVO> formPage(@Param("formQuery") FormQuery formQuery, @Param("corpId") String corpId);

    /**
     * 查询表单详情
     *
     * @param id     表单id
     * @param corpId 企业id
     * @return FormDetailViewVO
     */
    FormDetailViewVO selectFormDetail(@Param("id") Long id, @Param("corpId") String corpId);

    /**
     * 表单数据总览
     *
     * @param id        表单id
     * @param corpId    企业id
     * @param beginTime 开始日期
     * @param endTime   结束日期
     * @return
     */
    FormTotalView selectTotalView(@Param("id") Long id, @Param("corpId") String corpId, @Param("beginTime") LocalDate beginTime, @Param("endTime") LocalDate endTime);

    /**
     * 批量删除表单
     * @param deleteIdList 表单id列表
     */
    void deleteBatch(@Param("deleteIdList") List<Long> deleteIdList);

    /**
     * 通过id查询表单 忽略逻辑删除
     *
     * @param formId
     * @return
     */
    WeForm selectByIdIgnoreDelete(@Param("formId") Long formId);

    /**
     * 获取素材库表单
     *
     * @param formId    表单id
     * @return
     */
    WeFormMaterial getFormMaterial(@Param("formId") Long formId);
}

