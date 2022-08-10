package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeOperationsCenterGroupSopFilterCycleEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 群SOP筛选群聊条件-起止时间
 *
 * @author Society my sister Li
 * @date 2021-11-30 14:05:23
 */
@Repository
public interface WeOperationsCenterGroupSopFilterCycleMapper extends BaseMapper<WeOperationsCenterGroupSopFilterCycleEntity> {

    /**
     * 保存或更新数据
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param cycleStart 循环起始时间
     * @param cycleEnd   循环结束时间
     */
    void saveOrUpdate(@Param("corpId") String corpId, @Param("sopId") Long sopId, @Param("cycleStart") String cycleStart, @Param("cycleEnd") String cycleEnd);
}
