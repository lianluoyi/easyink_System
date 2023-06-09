package com.easyink.quartz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 任务状态处理
 * @date 2021/4/12 14:39
 **/
@Slf4j
@Component("TaskFissionStatusTask")
public class TaskFissionStatusTask {


    public void taskFissionExpiredStatusHandle() {
        log.info("任务宝过期时间处理--------------------------start");
        log.info("任务宝过期时间处理--------------------------end");
    }
}
