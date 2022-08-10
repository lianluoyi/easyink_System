package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeOperationsCenterGroupSopFilterEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 群SOP筛选群聊条件
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Repository
public interface WeOperationsCenterGroupSopFilterMapper extends BaseMapper<WeOperationsCenterGroupSopFilterEntity> {

    /**
     * 保存或更新数据
     *
     * @param sopFilterEntity sopFilterEntity
     */
    void saveOrUpdateGroupSopFilter(WeOperationsCenterGroupSopFilterEntity sopFilterEntity);

    /**
     * 根据corpId和sopId查询筛选条件
     *
     * @param corpId 企业ID
     * @param sopId  sopId
     * @return WeOperationsCenterGroupSopFilterEntity
     */
    WeOperationsCenterGroupSopFilterEntity getDataByCorpIdAndSopId(@Param("corpId") String corpId, @Param("sopId") Long sopId);
}
