package com.easyink.wecom.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskResultEntity;

import java.util.Date;

/**
 * 类名： 接口
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
public interface WeMomentTaskResultService extends IService<WeMomentTaskResultEntity> {

    /**
     * 更新发布信息
     * @param taskId 任务id
     * @param userId 用户id
     * @param publishStatus 发布状态
     * @param publishTime 发布时间
     */
    void updatePublishInfo(Long taskId, String userId, Integer publishStatus, Date publishTime);

}

