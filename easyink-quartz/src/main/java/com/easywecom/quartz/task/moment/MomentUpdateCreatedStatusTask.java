package com.easywecom.quartz.task.moment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.enums.moment.MomentStatusEnum;
import com.easywecom.wecom.domain.entity.moment.WeMomentTaskEntity;
import com.easywecom.wecom.service.moment.WeMomentTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类名： 更新企业朋友圈创建结果
 *
 * @author 佚名
 * @date 2022/1/11 19:29
 */
@Slf4j
@Component("momentUpdateCreatedStatusTask")
public class MomentUpdateCreatedStatusTask {
    private final WeMomentTaskService weMomentTaskService;


    @Autowired
    public MomentUpdateCreatedStatusTask(WeMomentTaskService weMomentTaskService) {
        this.weMomentTaskService = weMomentTaskService;
    }

    /**
     * 更新企业朋友圈创建结果
     */
    public void updateMomentTaskStatus() {
        //查询所有未创建完成的企业朋友圈任务
        List<WeMomentTaskEntity> taskEntities = weMomentTaskService.list(new LambdaQueryWrapper<WeMomentTaskEntity>()
                .eq(WeMomentTaskEntity::getType, 0)
                .and(i ->i.eq(WeMomentTaskEntity::getStatus, MomentStatusEnum.START.getType()).or()
                        .eq(WeMomentTaskEntity::getStatus,MomentStatusEnum.PROCESS.getType())));
        for (WeMomentTaskEntity taskEntity : taskEntities) {
            try {
                weMomentTaskService.updateTaskStatus(taskEntity);
                log.info("更新企业朋友圈创建任务执行成功 corpId:{}, taskId:{}", taskEntity.getCorpId(), taskEntity.getId());
            }catch (Exception e){
                log.error("更新企业朋友圈创建任务执行失败corpId:{}, taskId:{}", taskEntity.getCorpId(), taskEntity.getId());
            }
        }
    }
}
