package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeCustomerAcquisitionClient;
import com.easyink.wecom.domain.WeEmpleCodeSituation;
import com.easyink.wecom.domain.dto.emplecode.CustomerAssistantResp;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeEmpleCodeSituationService;
import com.easyink.wecom.utils.redis.CustomerAssistantRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获客助手-主页获客情况定时统计任务
 *
 * @author lichaoyu
 * @date 2023/8/28 10:39
 */
@Slf4j
@Component("customerAssistantSituationTask")
public class CustomerAssistantSituationTask {

    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerAcquisitionClient weCustomerAcquisitionClient;
    private final WeEmpleCodeSituationService weEmpleCodeSituationService;
    private final CustomerAssistantRedisCache customerAssistantRedisCache;

    public CustomerAssistantSituationTask(WeCorpAccountService weCorpAccountService, WeCustomerAcquisitionClient weCustomerAcquisitionClient, WeEmpleCodeSituationService weEmpleCodeSituationService, CustomerAssistantRedisCache customerAssistantRedisCache) {
        this.weCorpAccountService = weCorpAccountService;
        this.weCustomerAcquisitionClient = weCustomerAcquisitionClient;
        this.weEmpleCodeSituationService = weEmpleCodeSituationService;
        this.customerAssistantRedisCache = customerAssistantRedisCache;
    }

    public void getCustomerAssistantSituationData() {
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    // 获取当前企业下的获客情况
                    CustomerAssistantResp.Quota quota = weCustomerAcquisitionClient.quota(weCorpAccount.getCorpId());
                    if (quota == null) {
                        log.info("[获客助手] 未从企微获取到企业获客助手链接额度信息--------> corpId:{}", weCorpAccount.getCorpId());
                        return;
                    }
                    // 获取当前企业下的获客情况信息
                    WeEmpleCodeSituation situation = weEmpleCodeSituationService.getById(weCorpAccount.getCorpId());
                    if (situation == null) {
                        situation = new WeEmpleCodeSituation(weCorpAccount.getCorpId(), quota.getTotal(), quota.getBalance());
                    } else {
                        // 将今日新增客户数清零
                        situation.handleTaskData(quota.getTotal(), quota.getBalance());
                    }
                    // 更新获客情况信息
                    weEmpleCodeSituationService.saveOrUpdate(situation);
                    // 删除前一天的缓存今日新增客户数
                    customerAssistantRedisCache.delTodayNewCustomerCnt(weCorpAccount.getCorpId(), DateUtils.getYesterdayDateBeforeNow());
                }
            } catch (Exception e) {
                log.info("[获客助手] 获客情况定时任务执行异常, corpId:{}, ex:{}", weCorpAccount.getCorpId(), ExceptionUtils.getStackTrace(e));
            }
        });
    }
}
