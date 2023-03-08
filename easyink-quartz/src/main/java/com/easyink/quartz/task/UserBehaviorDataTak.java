package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author admin
 * @description 联系客户统计
 * @date 2021/2/24 0:41
 **/
@Slf4j
@Component("UserBehaviorDataTak")
public class UserBehaviorDataTak {

    private final WeCorpAccountService weCorpAccountService;
    private final WeUserService weUserService;

    @Autowired
    public UserBehaviorDataTak(WeCorpAccountService weCorpAccountService, WeUserService weUserService) {
        this.weCorpAccountService = weCorpAccountService;
        this.weUserService = weUserService;
    }

    public void getUserBehaviorData() {
        log.info("联系客户统计数据拉取-定时任务开始执行------>");
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    weUserService.getUserBehaviorDataByCorpId(weCorpAccount.getCorpId());
                }
            } catch (Exception e) {
                log.error("[获取用户数据]统计客户任务异常,corpid:{},e:{}", weCorpAccount.getCorpId(), ExceptionUtils.getStackTrace(e));
            }
        });
        log.info("定时任务执行完成------>");
    }

}
