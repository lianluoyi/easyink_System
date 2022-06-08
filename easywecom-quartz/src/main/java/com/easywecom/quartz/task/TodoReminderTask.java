package com.easywecom.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.enums.CustomerTrajectoryEnums;
import com.easywecom.common.enums.MessageType;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeCustomerTrajectory;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.message.TextMessageDTO;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeCustomerService;
import com.easywecom.wecom.service.WeCustomerTrajectoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 类名: 待办事件提醒任务
 *
 * @author : silver_chariot
 * @date : 2021/11/19 14:16
 */
@Component("todoReminderTask")
@Slf4j
public class TodoReminderTask {
    /**
     * 提示语模板
     */
    private final static String REMIND_MODEL = "【待办提示】\n时间: 【开始时间】~【截止时间】\n客户: 【客户昵称】\n内容: 【内容】";
    private final static String START_TIME = "【开始时间】";
    private final static String END_TIME = "【截止时间】";
    private final static String NICK_NAME = "【客户昵称】";
    private final static String CONTENT = "【内容】";

    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerService weCustomerService;
    private final WeMessagePushClient weMessagePushClient;

    @Autowired
    public TodoReminderTask(@NotNull WeCustomerTrajectoryService weCustomerTrajectoryService, @NotNull WeCorpAccountService weCorpAccountService,
                            @NotNull WeCustomerService weCustomerService, @NotNull WeMessagePushClient weMessagePushClient) {
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
        this.weCorpAccountService = weCorpAccountService;
        this.weCustomerService = weCustomerService;
        this.weMessagePushClient = weMessagePushClient;
    }

    /**
     * 执行任务
     */
    public void execute() {
        Date startTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        Date endTime = calendar.getTime();

        // 获取当天时间 格式 YYYY-MM-DD
        String nowDate = DateUtils.getDate();
        // 获取开始时间 格式 HH:mm:ss
        String startTimeStr = DateUtils.parseDateToStr(DateUtils.HH_MM_SS, startTime);
        // 获取结束时间 格式 HH:mm:ss
        String endTimeStr = DateUtils.parseDateToStr(DateUtils.HH_MM_SS, endTime);

        // 查询10分钟内 所有未通知的待办事项
        List<WeCustomerTrajectory> todoList = weCustomerTrajectoryService.list(new LambdaQueryWrapper<WeCustomerTrajectory>()
                .eq(WeCustomerTrajectory::getCreateDate, nowDate)
                .eq(WeCustomerTrajectory::getStatus, CustomerTrajectoryEnums.TodoTaskStatusEnum.NORMAL.getCode())
                .gt(WeCustomerTrajectory::getStartTime, startTimeStr)
                .lt(WeCustomerTrajectory::getStartTime, endTimeStr)
        );
        if (CollectionUtils.isEmpty(todoList)) {
            return;
        }
        // 对所有10分钟内未提示的待办事项发送 提示
        for (WeCustomerTrajectory todo : todoList) {
            try {
                sendRemind(todo);
            } catch (Exception e) {
                log.error("[TodoReminderTask]待办任务执行异常,todo:{}", todo);
            }
        }

    }

    /**
     * 发送提示
     *
     * @param todo {@link WeCustomerTrajectory}
     */
    private void sendRemind(WeCustomerTrajectory todo) {
        if (todo == null || StringUtils.isAnyBlank(todo.getCorpId(), todo.getExternalUserid(), todo.getUserId())) {
            log.error("[TodoReminderTask]待办任务提醒异常:参数缺失,todo:{}", todo);
            return;
        }
        String corpId = todo.getCorpId();
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (null == corpId || null == corpAccount || StringUtils.isBlank(corpAccount.getAgentId())) {
            log.error("[TodoReminderTask]待办任务提醒异常:获取企业详情异常,todo:{}", todo);
            return;
        }
        WeCustomer customer = weCustomerService.selectWeCustomerById(todo.getExternalUserid(), todo.getCorpId());
        // 1.组装提示语
        String content = REMIND_MODEL
                .replace(START_TIME, todo.getStartTime() == null ? StringUtils.EMPTY : todo.getStartTime().toString())
                .replace(END_TIME, todo.getEndTime() == null ? StringUtils.EMPTY : todo.getEndTime().toString())
                .replace(NICK_NAME, customer.getName())
                .replace(CONTENT, todo.getContent());
        // 2.构建企业API请求并发送
        TextMessageDTO contentInfo = TextMessageDTO.builder()
                .content(content)
                .build();
        WeMessagePushDTO request = WeMessagePushDTO.builder()
                .msgtype(MessageType.TEXT.getMessageType())
                .touser(todo.getUserId())
                .text(contentInfo)
                .agentid(Integer.valueOf(corpAccount.getAgentId()))
                .build();
        weMessagePushClient.sendMessageToUser(request, corpAccount.getAgentId(), corpId);
        // 3.设置待办事项状态为已通知
        todo.setStatus(CustomerTrajectoryEnums.TodoTaskStatusEnum.INFORMED.getCode());
        weCustomerTrajectoryService.updateById(todo);
        log.info("[TodoReminderTask]待办事项提示发送成功,todo:{}", todo);
    }
}
