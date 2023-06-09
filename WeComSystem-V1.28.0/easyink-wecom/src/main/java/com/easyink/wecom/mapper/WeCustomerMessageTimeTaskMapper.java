package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeCustomerMessageTimeTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群发消息 定时任务表 Mapper接口
 *
 * @author admin
 * @date 2021-02-09
 */
@Repository
public interface WeCustomerMessageTimeTaskMapper extends BaseMapper<WeCustomerMessageTimeTask> {

    /**
     * 查询群发任务列表
     *
     * @param timeMillis 当前时间毫秒数
     * @return {@link WeCustomerMessageTimeTask}s
     */
    List<WeCustomerMessageTimeTask> selectWeCustomerMessageTimeTaskGteSettingTime(@Param("timeMillis") long timeMillis);

    /**
     * 查询定时任务
     *
     * @param messageId 消息id
     * @return
     */
    WeCustomerMessageTimeTask getTimeTask(Long messageId);

    /**
     * 保存群发任务
     *
     * @param customerMessageTimeTask 群发任务信息
     * @return int
     */
    int saveWeCustomerMessageTimeTask(WeCustomerMessageTimeTask customerMessageTimeTask);

    /**
     * 更新
     *
     * @param customerMessageTimeTask 群发任务信息
     * @return 受影响行
     */
    int updateById(WeCustomerMessageTimeTask customerMessageTimeTask);

    /**
     * 更新群发定时任务处理状态
     *
     * @param taskId 任务id
     * @return int
     */
    int updateTaskSolvedById(@Param("taskId") Long taskId);

}
