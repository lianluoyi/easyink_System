package com.easyink.quartz.task;

import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.handle.job.EmpleStatisticHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 活码统计-定时统计任务
 *
 * @author lichaoyu
 * @date 2023/7/7 13:48
 */
@Slf4j
@Component("EmpleStatisticTask")
public class EmpleStatisticTask {

    private final EmpleStatisticHandle empleStatisticHandle;


    public EmpleStatisticTask(EmpleStatisticHandle empleStatisticHandle) {
        this.empleStatisticHandle = empleStatisticHandle;
    }

    /**
     * 活码统计-每日定时统计任务
     *
     * @param date 日期 格式为YYYY-MM-DD，当传入时，统计对应日期下的活码数据，不传默认前一天的数据
     */
    public void getEmpleStatisticDateData(String date) {
        String realDate;
        if (StringUtils.EMPTY.equals(date)) {
            realDate = DateUtils.getYesterdayDateBeforeNow();
        } else {
            realDate = date;
        }
        // 处理对应日期下的统计数据
        empleStatisticHandle.handle(realDate, null);
    }


}
