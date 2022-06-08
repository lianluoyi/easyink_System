package com.easywecom.quartz.task;

import com.easywecom.common.utils.DateUtils;
import com.easywecom.wecom.client.WeExternalContactClient;
import com.easywecom.wecom.domain.WeEmpleCode;
import com.easywecom.wecom.domain.dto.WeExternalContactDTO;
import com.easywecom.wecom.service.WeEmpleCodeService;
import com.easywecom.wecom.service.WeEmpleCodeTagService;
import com.easywecom.wecom.service.WeEmpleCodeUseScopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 类名：EmpleCodeThroughFriendTimeSwitchTask
 *
 * @author Society my sister Li
 * @date 2021-11-03 20:29
 */
@Slf4j
@Component("EmpleCodeThroughFriendTimeSwitchTask")
public class EmpleCodeThroughFriendTimeSwitchTask {

    private final WeEmpleCodeService weEmpleCodeService;
    private final WeExternalContactClient weExternalContactClient;
    private final WeEmpleCodeUseScopService weEmpleCodeUseScopService;
    private final WeEmpleCodeTagService weEmpleCodeTagService;

    @Autowired
    public EmpleCodeThroughFriendTimeSwitchTask(WeEmpleCodeService weEmpleCodeService, WeExternalContactClient weExternalContactClient, WeEmpleCodeUseScopService weEmpleCodeUseScopService, WeEmpleCodeTagService weEmpleCodeTagService) {
        this.weEmpleCodeService = weEmpleCodeService;
        this.weExternalContactClient = weExternalContactClient;
        this.weEmpleCodeUseScopService = weEmpleCodeUseScopService;
        this.weEmpleCodeTagService = weEmpleCodeTagService;
    }

    /**
     * 通过好友的开启关闭时间检测
     * cron: 0 * * * * ?
     */
    public void empleCodeThroughFriendTimeSwitch() {
        log.info("empleCodeThroughFriendTimeSwitch定时任务开始执行------>");
        //当前时间HH:mm
        String hmTime = DateUtils.parseDateToStr(DateUtils.HH_MM, new Date());


        //查询时间段通过的、未被删除的员工活码数据
        List<WeEmpleCode> weEmpleCodeList = weEmpleCodeService.getWeEmpleCodeByEffectTime(hmTime);
        if (CollectionUtils.isNotEmpty(weEmpleCodeList)) {
            WeExternalContactDTO.WeContactWay weContactWay;
            for (WeEmpleCode weEmpleCode : weEmpleCodeList) {
                try {
                    weContactWay = new WeExternalContactDTO.WeContactWay();
                    weContactWay.setConfig_id(weEmpleCode.getConfigId());
                    //hmTime==开始时间 => open
                    weContactWay.setSkip_verify(hmTime.equals(weEmpleCode.getEffectTimeOpen()));
                    weExternalContactClient.updateContactWay(weContactWay, weEmpleCode.getCorpId());
                } catch (Exception e) {
                    log.error("empleCodeThroughFriendTimeSwitch error!! id={},corpId={},e={}", weEmpleCode.getId(), weEmpleCode.getCorpId(), ExceptionUtils.getStackTrace(e));
                }
            }
            log.info("empleCodeThroughFriendTimeSwitch定时任务执行完成!");
        }
    }
}
