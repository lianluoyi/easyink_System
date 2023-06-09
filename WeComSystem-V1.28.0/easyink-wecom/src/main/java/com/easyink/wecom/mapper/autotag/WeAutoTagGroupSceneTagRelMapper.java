package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagGroupSceneTagRel;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群标签场景与标签关系表(WeAutoTagGroupSceneTagRel)表数据库访问层
 *
 * @author tigger
 * @since 2022-02-27 15:52:35
 */
@Repository
public interface WeAutoTagGroupSceneTagRelMapper extends BaseMapper<WeAutoTagGroupSceneTagRel> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagGroupSceneTagRel> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagGroupSceneTagRel> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagGroupSceneTagRel> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagGroupSceneTagRel> entities);


    /**
     * 查询标签列表通过规则id
     * select抓取接口
     *
     * @param ruleId
     * @return
     */
    List<TagInfoVO> listTagListByRuleId(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);
    /**
     * 查询标签名称列表通过规则id
     * select抓取接口
     *
     * @param ruleId
     * @return
     */
    List<String> listTagNameListByRuleId(@Param("ruleId") Long ruleId, @Param("corpId") String corpId);

    /**
     * 查询群场景标签表通过群场景id
     * select抓取接口
     *
     * @param groupSceneId
     * @param corpId
     * @return
     */
    List<TagInfoVO> listTagListByGroupSceneId(@Param("groupSceneId") Long groupSceneId, @Param("corpId") String corpId);

}

