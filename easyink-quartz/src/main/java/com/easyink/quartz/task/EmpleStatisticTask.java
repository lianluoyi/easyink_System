package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.ExceptionUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeEmpleCodeAnalyseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 活码统计-定时统计任务
 *
 * @author lichaoyu
 * @date 2023/7/7 13:48
 */
@Slf4j
@Component("EmpleStatisticTask")
public class EmpleStatisticTask {

    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper;
    private final WeCorpAccountService weCorpAccountService;


    public EmpleStatisticTask(WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper, WeCorpAccountService weCorpAccountService) {
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.weEmpleCodeStatisticMapper = weEmpleCodeStatisticMapper;
        this.weCorpAccountService = weCorpAccountService;
    }

    /**
     * 活码统计-小时定时任务
     *
     */
    public void getEmpleStatisticData() {
        // 获取当前时间前一小时的日期
        String beforeHourDate = DateUtils.getBeforeHourDate();
        // 处理对应日期下的统计数据
        handleDateData(beforeHourDate);
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
        handleDateData(realDate);
    }

    /**
     * 处理对应日期下的统计数据
     *
     * @param date 日期 格式为YYYY-MM-DD
     */
    private void handleDateData(String date) {
        if (StringUtils.isBlank(date)) {
            return;
        }
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        // 统计每个企业下，活码统计表的数据
        weCorpAccountList.forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    log.info("[活码统计] 活码统计定时任务开始执行--------> corpId:{}, date:{}", weCorpAccount.getCorpId(), date);
                    // 获取当前企业下所有有新增数据的活码-员工对应的统计数据
                    List<WeEmpleCodeStatistic> todayData = weEmpleCodeAnalyseService.getEmpleStatisticData(weCorpAccount.getCorpId(), date);
                    if (CollectionUtils.isEmpty(todayData)) {
                        return;
                    }
                    // 分批批量插入或更新今天的数据
                    BatchInsertUtil.doInsert(todayData, list -> weEmpleCodeStatisticMapper.batchInsertOrUpdate(todayData));
                }
            } catch (Exception e) {
                log.info("[活码统计] 活码统计定时任务执行异常, corpId:{}, date:{}, ex:{}", weCorpAccount.getCorpId(), date, ExceptionUtil.getExceptionMessage(e));
            } finally {
                log.info("[活码统计] 活码统计定时任务执行完成--------> corpId:{}, date:{}", weCorpAccount.getCorpId(), date);
            }
        });
    }
}
