package com.easyink.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeUserCustomerMessageStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据统计定时任务
 * 统计we_user_behavior_data表中new_contact_speak_cnt,replied_within_thirty_min_customer_cnt字段 用在数据统计-客户联系-客户概况
 * 统计we_user_customer_message_statistics表 用作数据统计-客户联系-客户活跃度 和 数据统计-员工服务
 *
 * @author wx
 * 2023/2/13 9:15
 **/
@Slf4j
@Component("DataStatisticsTask")
@RequiredArgsConstructor
public class DataStatisticsTask {

    private final WeCorpAccountService weCorpAccountService;
    private final WeUserCustomerMessageStatisticsService userCustomerMessageStatisticsService;

    public void getDataStatistics() {
        log.info("数据统计定时任务开始执行------>");
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.parallelStream().forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    // 开始统计
                    userCustomerMessageStatisticsService.getMessageStatistics(weCorpAccount.getCorpId(), DateUtils.dateTime(DateUtils.getYesterday(DateUtils.getNowDate())));
                }
            } catch (Exception e) {
                log.error("[数据统计定时任务]统计任务异常,corpId:{},e:{}", weCorpAccount.getCorpId(), ExceptionUtils.getStackTrace(e));
            }
        });
        log.info("数据统计定时任务执行完成------>");
    }

}
