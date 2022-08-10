package com.easyink.quartz.task.moment;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easyink.common.enums.moment.MomentPublishStatusEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskEntity;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskResultEntity;
import com.easyink.wecom.service.moment.WeMomentTaskResultService;
import com.easyink.wecom.service.moment.WeMomentTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名： 企业朋友圈执行状态任务
 *
 * @author 佚名
 * @date 2022/1/11 20:38
 */
@Slf4j
@Component("momentPublishStatusTask")
public class MomentPublishStatusTask {
    private final WeMomentTaskService weMomentTaskService;
    private final WeMomentTaskResultService weMomentTaskResultService;

    @Autowired
    public MomentPublishStatusTask(WeMomentTaskService weMomentTaskService,  WeMomentTaskResultService weMomentTaskResultService) {
        this.weMomentTaskService = weMomentTaskService;
        this.weMomentTaskResultService = weMomentTaskResultService;
    }

    public void updateMomentPublishStatus() {
        //查询发送时间48小时内已完成创建的任务 sendTime  >= currentTime - 48h
        Date subDay = DateUtils.dateSubHour(new Date(), 48);
        List<WeMomentTaskEntity> taskList = weMomentTaskService.listOfNotPublish(subDay, false);
        for (WeMomentTaskEntity task : taskList) {
            try {
                weMomentTaskService.updatePublishStatus(task.getMomentId(), task.getId(), task.getCorpId());
            }catch (Exception e){
                log.error("定时任务更新企业朋友圈发布状态失败，corpId:{},momentTaskId:{}",task.getCorpId(),task.getMomentId());
            }
        }
        //查询发布时间48小时前的任务 sendTime  < currentTime - 48h
        List<WeMomentTaskEntity> expireTask = weMomentTaskService.listOfNotPublish(subDay, true);
        //更新执行结果为已过期
        if (CollectionUtils.isEmpty(expireTask)) {
            log.info("没有过期朋友圈任务，停止执行");
            return;
        }
        List<Long> taskId = expireTask.stream().map(WeMomentTaskEntity::getId).collect(Collectors.toList());
        weMomentTaskResultService.update(new LambdaUpdateWrapper<WeMomentTaskResultEntity>()
                .eq(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.NOT_PUBLISH.getType())
                .in(WeMomentTaskResultEntity::getMomentTaskId, taskId)
                .set(WeMomentTaskResultEntity::getPublishStatus, MomentPublishStatusEnum.EXPIRE.getType()));
        weMomentTaskService.update(new LambdaUpdateWrapper<WeMomentTaskEntity>()
                .in(WeMomentTaskEntity::getId, taskId)
                .set(WeMomentTaskEntity::getUpdateTime, new Date()));
    }
}
