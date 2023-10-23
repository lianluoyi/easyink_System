package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.dto.message.AsyncResultDTO;
import com.easyink.wecom.mapper.WeCustomerMessgaeResultMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeCustomerMessageOriginalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 类名： 消息结果定时任务
 *
 * @author 佚名
 * @date 2021/11/16 14:32
 */
@Slf4j
@Component("MessageResultTask")
public class MessageResultTask {
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerMessageOriginalService weCustomerMessageOriginalService;
    private final WeCustomerMessgaeResultMapper weCustomerMessgaeResultMapper;
    @Resource(name = "messageResultTaskExecutor")
    private ThreadPoolTaskExecutor messageResultTaskExecutor;
    private static final int SUB_DAY = 30;

    @Autowired
    public MessageResultTask(WeCorpAccountService weCorpAccountService, WeCustomerMessageOriginalService weCustomerMessageOriginalService, WeCustomerMessgaeResultMapper weCustomerMessgaeResultMapper) {
        this.weCorpAccountService = weCorpAccountService;
        this.weCustomerMessageOriginalService = weCustomerMessageOriginalService;
        this.weCustomerMessgaeResultMapper = weCustomerMessgaeResultMapper;
    }


    public void asyncSendResult() {
        log.info("MessageResultTask定时任务开始执行------>");
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (CollectionUtils.isEmpty(weCorpAccountList)) {
            log.warn("群发定时更新未发送消息结果--->没有可用的企业配置");
            return;
        }
        String corpId;
        for (WeCorpAccount weCorpAccount : weCorpAccountList) {
            corpId = weCorpAccount.getCorpId();
            if (StringUtils.isBlank(corpId)) {
                continue;
            }
            Date endTime = new Date();
            Date startTime = DateUtils.dateSubDay(endTime, SUB_DAY);
            List<AsyncResultDTO> resultDtoList = weCustomerMessgaeResultMapper.listOfNotSend(corpId, startTime, endTime);
            for (AsyncResultDTO asyncResultDTO : resultDtoList) {
                String finalCorpId = corpId;
                messageResultTaskExecutor.execute(() -> {
                    try {
                        asyncResultDTO.setMsgids(Arrays.asList(asyncResultDTO.getMsgArray()));
                        weCustomerMessageOriginalService.asyncResult(asyncResultDTO, finalCorpId);
                    } catch (JsonProcessingException e) {
                        log.error("MessageResultTask定时任务异常 ex:{}，messageId:{}", ExceptionUtils.getStackTrace(e), asyncResultDTO.getMessageId());
                    }
                });
            }
        }
    }
}
