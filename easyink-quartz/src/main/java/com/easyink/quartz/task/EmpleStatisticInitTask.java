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
import com.easyink.wecom.service.WeEmpleCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 活码统计-初始化数据定时任务
 *
 * @author lichaoyu
 * @date 2023/7/19 10:20
 */
@Slf4j
@Component("EmpleStatisticInitTask")
public class EmpleStatisticInitTask {

    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper;
    private final WeCorpAccountService weCorpAccountService;
    private final WeEmpleCodeService weEmpleCodeService;

    public EmpleStatisticInitTask(WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper, WeCorpAccountService weCorpAccountService, WeEmpleCodeService weEmpleCodeService) {
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.weEmpleCodeStatisticMapper = weEmpleCodeStatisticMapper;
        this.weCorpAccountService = weCorpAccountService;
        this.weEmpleCodeService = weEmpleCodeService;
    }

    /**
     * 活码统计-初始化定时任务
     *
     */
    public void getEmpleStatisticData() {
        // 获取当天的时间
        String today = DateUtils.dateTime(new Date());
        // 初始化当天日期下的统计数据
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        // 统计每个企业下，活码统计表的数据
        weCorpAccountList.forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    log.info("[活码统计] 每日活码初始化数据定时任务开始执行--------> corpId:{}, date:{}", weCorpAccount.getCorpId(), today);
                    // 获取活码ID列表
                    List<Long> empleCodeIdList = weEmpleCodeService.getEffectEmpleCodeId(weCorpAccount.getCorpId(), today);
                    if (CollectionUtils.isEmpty(empleCodeIdList)) {
                        return;
                    }
                    // 初始化一份数据为0的记录
                    List<WeEmpleCodeStatistic> todayData = weEmpleCodeAnalyseService.initData(weCorpAccount.getCorpId(), empleCodeIdList, today);
                    // 获取前一天的活码数据
                    List<WeEmpleCodeStatistic> yesterdayData = weEmpleCodeAnalyseService.getEmpleStatisticData(weCorpAccount.getCorpId(), DateUtils.getYesterday(today, DateUtils.YYYY_MM_DD), empleCodeIdList);
                    // 将前一天的累计客户数和留存客户数设置到初始化的数据中
                    todayData.forEach(item -> {
                        for (WeEmpleCodeStatistic yesterday : yesterdayData) {
                            if (item.getEmpleCodeId().equals(yesterday.getEmpleCodeId()) && item.getUserId().equals(yesterday.getUserId())) {
                                item.setAccumulateCustomerCnt(yesterday.getAccumulateCustomerCnt());
                                item.setRetainCustomerCnt(yesterday.getRetainCustomerCnt());
                            }
                        }
                    });
                    if (CollectionUtils.isEmpty(todayData)) {
                        return;
                    }
                    // 分批批量插入今日初始化的数据
                    BatchInsertUtil.doInsert(todayData, list -> weEmpleCodeStatisticMapper.batchInsertOrUpdate(todayData));
                }
            } catch (Exception e) {
                log.info("[活码统计] 每日活码初始化数据定时任务执行异常, corpId:{}, date:{}, ex:{}", weCorpAccount.getCorpId(), today, ExceptionUtil.getExceptionMessage(e));
            } finally {
                log.info("[活码统计] 每日活码初始化数据定时任务执行完成--------> corpId:{}, date:{}", weCorpAccount.getCorpId(), today);
            }
        });
    }

}
