package com.easyink.quartz.task.moment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.entity.moment.WeMomentDetailRelEntity;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskEntity;
import com.easyink.wecom.service.WeWordsDetailService;
import com.easyink.wecom.service.moment.WeMomentDetailRelService;
import com.easyink.wecom.service.moment.WeMomentTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名： MomentStartCreateTask
 *
 * @author 佚名
 * @date 2022/1/12 21:22
 */
@Slf4j
@Component("momentStartCreateTask")
public class MomentStartCreateTask {
    private final WeMomentTaskService weMomentTaskService;
    private final WeMomentDetailRelService weMomentDetailRelService;
    private final WeWordsDetailService weWordsDetailService;
    @Autowired
    public MomentStartCreateTask(WeMomentTaskService weMomentTaskService, WeMomentDetailRelService weMomentDetailRelService, WeWordsDetailService weWordsDetailService) {
        this.weMomentTaskService = weMomentTaskService;
        this.weMomentDetailRelService = weMomentDetailRelService;
        this.weWordsDetailService = weWordsDetailService;
    }

    public void createMoment(){
        //查询定时任务
        List<WeMomentTaskEntity> weMomentTaskEntities = weMomentTaskService.listOfSettingTask(new Date());
        if (CollectionUtils.isEmpty(weMomentTaskEntities)){
            log.info("不存在待发送定时朋友圈停止定时任务");
            return;
        }
        for (WeMomentTaskEntity weMomentTaskEntity : weMomentTaskEntities) {
            List<WeMomentDetailRelEntity> detailRelEntities = weMomentDetailRelService.list(new LambdaQueryWrapper<WeMomentDetailRelEntity>()
                    .eq(WeMomentDetailRelEntity::getMomentTaskId, weMomentTaskEntity.getId()));
            List<WeWordsDetailEntity> attachments =CollectionUtils.isNotEmpty(detailRelEntities)
                    ?weWordsDetailService.listByIds(detailRelEntities.stream().map(WeMomentDetailRelEntity::getDetailId).collect(Collectors.toList()))
                    :new ArrayList<>();
            if (CollectionUtils.isEmpty(attachments)&& StringUtils.isBlank(weMomentTaskEntity.getContent())){
                log.error("不存在发送附件停止执行发送朋友圈定时任务，corpId:{},taskId:{}",weMomentTaskEntity.getCorpId(),weMomentTaskEntity.getId());
               continue;
            }
            try{
                weMomentTaskService.startCreatMoment(weMomentTaskEntity,attachments);
            }catch (Exception e){
                log.error("执行定时发送朋友圈失败，corpId:{},momentTaskId:{}",weMomentTaskEntity.getCorpId(),weMomentTaskEntity.getId());
            }
        }
    }
}
