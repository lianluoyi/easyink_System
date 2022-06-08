package com.easywecom.quartz.task;

import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.enums.MessageType;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.WeGroupCode;
import com.easywecom.wecom.domain.WeGroupCodeActual;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.message.TextMessageDTO;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeGroupCodeActualService;
import com.easywecom.wecom.service.WeGroupCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 类名： 客户群活码定时检查过期群码
 *
 * @author 佚名
 * @date 2021/11/5 18:39
 */
@Slf4j
@Component("GroupCodeActualTimeTask")
public class GroupCodeActualTimeTask {
    private static final String ADMIN = "admin";
    private final WeCorpAccountService weCorpAccountService;
    private final WeGroupCodeService weGroupCodeService;
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient messagePushClient;
    private final RedisCache redisCache;
    private final WeGroupCodeActualService weGroupCodeActualService;



    @Autowired
    public GroupCodeActualTimeTask(WeCorpAccountService weCorpAccountService, WeGroupCodeService weGroupCodeService, WeCorpAccountService corpAccountService, WeMessagePushClient messagePushClient, RedisCache redisCache, WeGroupCodeActualService weGroupCodeActualService) {
        this.weCorpAccountService = weCorpAccountService;
        this.weGroupCodeService = weGroupCodeService;
        this.corpAccountService = corpAccountService;
        this.messagePushClient = messagePushClient;
        this.redisCache = redisCache;
        this.weGroupCodeActualService = weGroupCodeActualService;
    }

    public void findExpireCode() {
        log.info("GroupCodeActualTimeTask定时任务开始执行------>");
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (CollectionUtils.isEmpty(weCorpAccountList)) {
            log.warn("客户群活码--->没有可用的企业配置");
            return;
        }
        String corpId;
        for (WeCorpAccount weCorpAccount : weCorpAccountList) {
            corpId = weCorpAccount.getCorpId();
            if (StringUtils.isBlank(corpId)) {
                continue;
            }
            List<WeGroupCode> weGroupCodeList = weGroupCodeService.selectExpireCode(corpId);
            List<WeGroupCodeActual> weGroupCodeActualList = new ArrayList<>();
            for (WeGroupCode weGroupCode : weGroupCodeList) {
                weGroupCode.setCorpId(corpId);
                try {
                    if (CollectionUtils.isNotEmpty(weGroupCode.getActualList())) {
                        if (!ADMIN.equals(weGroupCode.getCreateBy())) {
                            sendToUser(weGroupCode);
                        }
                        //设置过期key单位毫秒
                        weGroupCode.getActualList().forEach(weGroupCodeActual -> {
                            //当前时间大于过期时间已经过期了
                            if (new Date().compareTo(weGroupCodeActual.getEffectTime()) > 0) {
                                weGroupCodeActual.setStatus(WeConstans.WE_CUSTOMER_MSG_RESULT_DEFALE);
                                weGroupCodeActualList.add(weGroupCodeActual);
                            } else {
                                //过期时长
                                Long timeOut = weGroupCodeActual.getEffectTime().getTime() - System.currentTimeMillis();
                                redisCache.setCacheObject(WeConstans.getWeActualGroupCodeKey(weGroupCodeActual.getId().toString()), weGroupCodeActual.getId(), timeOut.intValue(), TimeUnit.MILLISECONDS);
                            }
                        });
                    }
                }catch (Exception e){
                    log.error("GroupCodeActualTimeTask ERROR!! corpId={},e={}", corpId, ExceptionUtils.getStackTrace(e));
                }
            }
            if (CollectionUtils.isNotEmpty(weGroupCodeActualList)) {
                weGroupCodeActualService.updateBatchById(weGroupCodeActualList);
            }
        }
        log.info("GroupCodeActualTimeTask定时任务执行完成!");
    }
    private void sendToUser(WeGroupCode weGroupCode){
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(weGroupCode.getCorpId());
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        StringBuilder content = new StringBuilder();
        //设置发送者 发送给企业员工
        pushDto.setTouser(weGroupCode.getCreateBy());
        content.append("您的客户群活码").append("“").append(weGroupCode.getActivityName()).append("”").append("下有客户群即将过期，如需继续使用该客户群，请及时更新");
        text.setContent(content.toString());
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.debug("发送客户群活码过期信息：toUser:{}", pushDto.getTouser());
        messagePushClient.sendMessageToUser(pushDto, agentId, weGroupCode.getCorpId());
    }
}
