package com.easyink.wecom.service.impl;

import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.entity.WeUserCustomerMessageStatistics;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 对话统计处理
 *
 * @author wx
 * 2023/2/14 11:39
 **/
@NoArgsConstructor
@Data
public class ContactStatisticsHandle {
    /**
     * 上一句话是员工讲话
     */
    private Boolean lastRoundIsUserSpeak;

    /**
     * 第一次收到员工发送消息的时间
     */
    private Long firstReceiveUserMessageTime;
    /**
     * 第一次回复员工消息的时间
     */
    private Long firstReplyUserMessageTime;

    /**
     * 三个轮次对话 所以需要六次发言
     */
    protected int sixTimes = 6;
    /**
     * 发言次数
     */
    private Integer rounds;
    /**
     * 第一次收到客户消息的时间
     */
    private Long firstReceiveExMessageTime;
    /**
     * 第一次回复客户消息的时间
     */
    private Long firstReplyExMessageTime;

    /**
     * 客户发言处理
     *
     * @param conversation  {@link ConversationArchiveVO}
     */
    public void customerSpeakHandle(ConversationArchiveVO conversation) {
        if (conversation == null) {
            return;
        }
        // 上一句是员工发言 加一个轮次
        if (lastRoundIsUserSpeak) {
            rounds ++;
            lastRoundIsUserSpeak = false;
        }
        if (firstReceiveExMessageTime == null) {
            // 员工第一次收到客户的消息
            firstReceiveExMessageTime = conversation.getMsgTime();
        }
        // 客户收到员工消息（firstReceiveUserMessageTime != null 说明客户已经收到了员工的消息） 后 第一次回复客户消息
        if (firstReceiveUserMessageTime != null && firstReplyUserMessageTime == null) {
            firstReplyUserMessageTime = conversation.getMsgTime();
        }
    }

    /**
     * 员工发言处理
     *
     * @param conversation  {@link ConversationArchiveVO}
     */
    public void userSpeakHandle(ConversationArchiveVO conversation) {
        if (conversation == null) {
            return;
        }
        // 上一句是员工发言 加一个轮次
        if (Boolean.FALSE.equals(lastRoundIsUserSpeak)) {
            rounds ++;
            lastRoundIsUserSpeak = true;
        }
        // 员工收到客户消息(firstReceiveExMessageTime != null 说明员工收到了客户消息) 后 员工第一次回复客户
        if (firstReceiveExMessageTime != null && firstReplyExMessageTime == null) {
            firstReplyExMessageTime = conversation.getMsgTime();
        }
        if (firstReceiveUserMessageTime == null) {
            // 客户第一次收到员工消息
            firstReceiveUserMessageTime = conversation.getMsgTime();
        }
    }

    /**
     * 刷新初始值
     */
    public void refresh() {
        lastRoundIsUserSpeak = false;
        firstReceiveUserMessageTime = null;
        firstReplyUserMessageTime = null;
        rounds = 0;
        firstReceiveExMessageTime = null;
        firstReplyExMessageTime = null;
    }


    /**
     * 保存统计结果
     *
     * @param userCustomerMessageStatistics {@link WeUserCustomerMessageStatistics}
     */
    public void saveStatisticsResult(WeUserCustomerMessageStatistics userCustomerMessageStatistics, Boolean isUserSpeak) {
        // 两者不为空说明  员工收到客户消息 且 回复客户
        if (firstReceiveExMessageTime != null && firstReplyExMessageTime != null){
            userCustomerMessageStatistics.setFirstReplyTimeIntervalAlterReceive(DateUtils.diffTimeReturnMin(new Date(firstReplyExMessageTime), new Date(firstReceiveExMessageTime)));
        }
        // 彼此交互的发送了六条消息 满足三个轮次
        if (rounds >= sixTimes) {
            userCustomerMessageStatistics.setThreeRoundsDialogueFlag(true);
        }
        // 当天员工首次给客户发消息 且 客户有回复给员工
        if (firstReceiveUserMessageTime != null && firstReplyUserMessageTime != null && isUserSpeak) {
            int thirtyMin = 30;
            if (DateUtils.diffTimeReturnMin(new Date(firstReplyUserMessageTime), new Date(firstReceiveUserMessageTime)) < thirtyMin){
                userCustomerMessageStatistics.setRepliedWithinThirtyMinCustomerFlag(true);
            }
        }else {
            userCustomerMessageStatistics.setRepliedWithinThirtyMinCustomerFlag(false);
        }
    }

    /**
     * 处理第一次发言
     *
     * @param conversation         {@link ConversationArchiveVO}
     * @param userCustomerMessageStatistics {@link WeUserCustomerMessageStatistics}
     * @param isUserFirst 判断第一次发言是否为员工
     */
    public void handleFirstSpeak(ConversationArchiveVO conversation, WeUserCustomerMessageStatistics userCustomerMessageStatistics,Boolean isUserFirst) {
        if (conversation == null || userCustomerMessageStatistics == null) {
            return;
        }
        // 对话是否由员工主动发起
        userCustomerMessageStatistics.setUserActiveDialogue(isUserFirst);
        lastRoundIsUserSpeak = isUserFirst;
        //增加一轮对话
        rounds++;
        //若为客户发言则设置首次收到客户消息时间
        if (!isUserFirst){
            // 员工第一次收到客户消息的时间
            firstReceiveExMessageTime = conversation.getMsgTime();
        }else {
            // 客户第一次收到员工消息的时间
            firstReceiveUserMessageTime = conversation.getMsgTime();
        }

    }

    /**
     * 是否结束统计
     *
     * @return
     */
    public boolean isEnd() {
        return rounds.compareTo(sixTimes) >= 0;
    }
}
