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
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
    private final EmpleStatisticRedisCache empleStatisticRedisCache;
    private final WeEmpleCodeService weEmpleCodeService;


    public EmpleStatisticTask(WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper, WeCorpAccountService weCorpAccountService, EmpleStatisticRedisCache empleStatisticRedisCache, WeEmpleCodeService weEmpleCodeService) {
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.weEmpleCodeStatisticMapper = weEmpleCodeStatisticMapper;
        this.weCorpAccountService = weCorpAccountService;
        this.empleStatisticRedisCache = empleStatisticRedisCache;
        this.weEmpleCodeService = weEmpleCodeService;
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
                    // 获取活码ID列表
                    List<Long> empleCodeIdList = weEmpleCodeService.getEffectEmpleCodeId(weCorpAccount.getCorpId(), date);
                    if (CollectionUtils.isEmpty(empleCodeIdList)) {
                        return;
                    }
                    // 获取当前企业下所有有新增数据的活码-员工对应的统计数据
                    List<WeEmpleCodeStatistic> todayData = weEmpleCodeAnalyseService.getEmpleStatisticData(weCorpAccount.getCorpId(), date, empleCodeIdList);
                    if (CollectionUtils.isEmpty(todayData)) {
                        return;
                    }
                    // 分批批量插入或更新今天的数据
                    BatchInsertUtil.doInsert(todayData, list -> weEmpleCodeStatisticMapper.batchInsertOrUpdate(todayData));
                    // 删除Redis中前一天的数据
                    deleteRedis(todayData, weCorpAccount.getCorpId(), date);
                }
            } catch (Exception e) {
                log.info("[活码统计] 活码统计定时任务执行异常, corpId:{}, date:{}, ex:{}", weCorpAccount.getCorpId(), date, ExceptionUtil.getExceptionMessage(e));
            } finally {
                log.info("[活码统计] 活码统计定时任务执行完成--------> corpId:{}, date:{}", weCorpAccount.getCorpId(), date);
            }
        });
    }

    /**
     * 删除Redis数据
     *
     * @param todayData 今日数据
     * @param corpId 企业ID
     * @param date      日期，格式为YYYY-MM-DD
     */
    private void deleteRedis(List<WeEmpleCodeStatistic> todayData, String corpId, String date) {
        // 获取活码ID
        List<Long> empleCodeIdList = todayData.stream().map(WeEmpleCodeStatistic::getEmpleCodeId).collect(Collectors.toList());
        // 获取员工ID
        List<String> userIdList = todayData.stream().map(WeEmpleCodeStatistic::getUserId).collect(Collectors.toList());
        // 删除Redis中的数据
        empleStatisticRedisCache.batchRemoveByDate(corpId, date, empleCodeIdList, userIdList);
    }
}
