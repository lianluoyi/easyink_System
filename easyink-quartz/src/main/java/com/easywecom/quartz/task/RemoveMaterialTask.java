package com.easywecom.quartz.task;

import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.wecom.domain.WeMaterialConfig;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeMaterialConfigService;
import com.easywecom.wecom.service.WeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 类名：RemoveMaterialTask
 *
 * @author Society my sister Li
 * @date 2021-10-13 17:14
 */
@Slf4j
@Component("RemoveMaterialTask")
public class RemoveMaterialTask {

    private final WeCorpAccountService weCorpAccountService;
    private final WeMaterialConfigService weMaterialConfigService;
    private final WeMaterialService weMaterialService;

    @Autowired
    public RemoveMaterialTask(WeCorpAccountService weCorpAccountService, WeMaterialConfigService weMaterialConfigService, WeMaterialService weMaterialService) {
        this.weCorpAccountService = weCorpAccountService;
        this.weMaterialConfigService = weMaterialConfigService;
        this.weMaterialService = weMaterialService;
    }

    /**
     * 移除过期素材定时任务
     * cron: 0 0 4 * * ?
     */
    public void removeExpireMaterial() {
        log.info("RemoveMaterialTask定时任务开始执行------>");
        WeMaterialConfig weMaterialConfig;
        Date lastRemoveDate;
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (CollectionUtils.isEmpty(weCorpAccountList)) {
            log.warn("没有可用的企业配置");
            return;
        }
        String corpId = null;
        for (WeCorpAccount weCorpAccount : weCorpAccountList) {
            try {
                corpId = weCorpAccount.getCorpId();
                if (StringUtils.isBlank(corpId)) {
                    continue;
                }
                //获取这个企业下的素材全局配置
                weMaterialConfig = weMaterialConfigService.findByCorpId(corpId);
                if (!weMaterialConfig.getIsDel()) {
                    continue;
                }
                Integer delDays = weMaterialConfig.getDelDays();
                lastRemoveDate = DateUtils.addDays(DateUtils.getNowDate(), (-1) * delDays);
                weMaterialService.removeMaterialByJob(corpId, lastRemoveDate);
            } catch (Exception e) {
                log.error("RemoveMaterialTask ERROR!! corpId={},e={}", corpId, ExceptionUtils.getStackTrace(e));
            }
        }
        log.info("RemoveMaterialTask定时任务执行完成!");
    }
}
