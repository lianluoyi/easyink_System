package com.easywecom.wecom.service;

import com.easywecom.wecom.domain.WeMessagePush;

import java.util.List;

/**
 * 消息发送的Service接口
 *
 * @author admin
 * @date 2020-10-28
 */
public interface WeMessagePushService {
    /**
     * 查询消息发送的
     *
     * @param messagePushId 消息发送的ID
     * @return 消息发送的
     */
    WeMessagePush selectWeMessagePushById(Long messagePushId);

    /**
     * 查询消息发送的列表
     *
     * @param weMessagePush 消息发送的
     * @return 消息发送的集合
     */
    List<WeMessagePush> selectWeMessagePushList(WeMessagePush weMessagePush);

    /**
     * 新增消息发送的
     *
     * @param weMessagePush 消息发送的
     * @return 结果
     */
    void insertWeMessagePush(WeMessagePush weMessagePush, String corpId);

    /**
     * 批量删除消息发送的
     *
     * @param messagePushIds 需要删除的消息发送的ID
     * @return 结果
     */
    int deleteWeMessagePushByIds(Long[] messagePushIds);

    /**
     * 删除消息发送的信息
     *
     * @param messagePushId 消息发送的ID
     * @return 结果
     */
    int deleteWeMessagePushById(Long messagePushId);
}
