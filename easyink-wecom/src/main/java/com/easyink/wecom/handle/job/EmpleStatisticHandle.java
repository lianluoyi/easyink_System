package com.easyink.wecom.handle.job;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.ExceptionUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeEmpleCodeAnalyseService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工活码报表handle
 *
 * @author tigger
 * 2025/6/11 16:31
 **/
@Slf4j
@Component
@AllArgsConstructor
public class EmpleStatisticHandle {
    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper;
    private final WeCorpAccountService weCorpAccountService;
    private final EmpleStatisticRedisCache empleStatisticRedisCache;
    private final WeEmpleCodeService weEmpleCodeService;

    /**
     * 处理对应日期下的统计数据
     *
     * @param date   日期 格式为YYYY-MM-DD
     * @param corpId 指定的企业id, 没有则查询所有
     */
    public void handle(String date, String corpId) {
        if (StringUtils.isBlank(date)) {
            return;
        }
        List<String> corpIdList = new ArrayList<>();
        if (StringUtils.isBlank(corpId)) {
            corpIdList.addAll(weCorpAccountService.listOfAuthCorpInternalWeCorpAccount().stream().map(WeCorpAccount::getCorpId).collect(Collectors.toList()));
        } else {
            corpIdList.add(corpId);
        }
        if(CollectionUtils.isEmpty(corpIdList)){
            log.info("[活码统计] 没有可执行的企业列表 date:{}", date);
            return;
        }
        // 统计每个企业下，活码统计表的数据
        corpIdList.forEach(it -> {
            try {
                if (StringUtils.isBlank(it)) {
                    return;
                }
                log.info("[活码统计] 活码统计定时任务开始执行--------> corpId:{}, date:{}", it, date);
                // 获取活码ID列表
                List<Long> empleCodeIdList = weEmpleCodeService.getEffectEmpleCodeId(it, date);
                if (CollectionUtils.isEmpty(empleCodeIdList)) {
                    return;
                }
                // 获取当前企业下所有有新增数据的活码-员工对应的统计数据
                List<WeEmpleCodeStatistic> todayData = weEmpleCodeAnalyseService.getEmpleStatisticData(it, date, empleCodeIdList);
                if (CollectionUtils.isEmpty(todayData)) {
                    return;
                }
                // 分批批量插入或更新今天的数据
                BatchInsertUtil.doInsert(todayData, list -> weEmpleCodeStatisticMapper.batchInsertOrUpdate(todayData));
                // 删除Redis中前一天的数据
                deleteRedis(todayData, it, date);
                log.info("[活码统计] 活码统计定时任务执行完成--------> corpId:{}, date:{}", it, date);
            } catch (Exception e) {
                log.info("[活码统计] 活码统计定时任务执行异常, corpId:{}, date:{}, ex:{}", it, date, ExceptionUtil.getExceptionMessage(e));
            }
        });
    }


    /**
     * 删除Redis数据
     *
     * @param todayData 今日数据
     * @param corpId    企业ID
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
