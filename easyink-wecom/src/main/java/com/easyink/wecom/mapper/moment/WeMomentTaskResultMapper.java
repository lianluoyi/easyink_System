package com.easyink.wecom.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskResultEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Repository
public interface WeMomentTaskResultMapper extends BaseMapper<WeMomentTaskResultEntity> {

    /**
     * 更新发布信息
     * @param momentTaskId 任务id
     * @param userId 用户id
     * @param publishStatus 发布状态
     * @param publishTime 发布时间
     */
    void updatePublishInfo(@Param("momentTaskId") Long momentTaskId, @Param("userId") String userId, @Param("publishStatus") Integer publishStatus, @Param("publishTime") Date publishTime);

}
