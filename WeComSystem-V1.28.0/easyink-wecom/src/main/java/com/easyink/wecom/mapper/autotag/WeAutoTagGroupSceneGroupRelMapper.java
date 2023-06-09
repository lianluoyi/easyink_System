package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagGroupSceneGroupRel;
import com.easyink.wecom.domain.vo.autotag.GroupInfoVO;
import com.easyink.wecom.domain.vo.autotag.group.GroupSceneRuleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群标签场景与群关系表(WeAutoTagGroupSceneGroupRel)表数据库访问层
 *
 * @author tigger
 * @since 2022-02-27 15:52:34
 */
@Repository
public interface WeAutoTagGroupSceneGroupRelMapper extends BaseMapper<WeAutoTagGroupSceneGroupRel> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagGroupSceneGroupRel> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeAutoTagGroupSceneGroupRel> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeAutoTagGroupSceneGroupRel> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeAutoTagGroupSceneGroupRel> entities);

    /**
     * 查询群场景群列表通过群场景id
     * select抓取接口
     *
     * @param groupSceneId
     * @param corpId
     * @return
     */
    List<GroupInfoVO> listGroupListByGroupSceneId(@Param("groupSceneId") Long groupSceneId, @Param("corpId") String corpId);


    /**
     * 查询包含该群的规则id列表
     *
     * @param corpId 企业id
     * @param chatId 群id
     * @return
     */
    List<GroupSceneRuleVO> selectRuleIdAndSceneIdMappingByChatId(@Param("corpId") String corpId, @Param("chatId") String chatId);
}

