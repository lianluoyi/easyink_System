package com.easyink.wecom.service;

import com.alibaba.fastjson.JSONObject;
import com.easyink.common.core.domain.ConversationArchiveQuery;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.github.pagehelper.PageInfo;

/**
 * @author admin
 * @description 会话存档业务接口
 * @date 2020/12/19 13:59
 **/
public interface WeConversationArchiveService {
    /**
     * 根据用户ID 获取对应内部联系人列表
     *
     * @param query      入参
     * @param /fromId    发送人id
     * @param /reveiceId 接收人id
     * @return
     */
    PageInfo<ConversationArchiveVO> getChatContactList(ConversationArchiveQuery query);

    PageInfo<ConversationArchiveVO> getChatRoomContactList(ConversationArchiveQuery query);

    /**
     * 查询最早聊天记录
     *
     * @param fromId 消息来源
     * @param receiveId 消息接收者
     * @return 聊天记录结果
     */
    JSONObject getFinalChatContactInfo(String fromId, String receiveId, String corpId);

    /**
     * 查询最早群聊记录
     *
     * @param fromId 消息来源
     * @param roomId 群id
     * @return 群聊天记录结果
     */
    JSONObject getFinalChatRoomContactInfo(String fromId, String roomId, String corpId);

    /**
     * 获取全局会话数据接口
     *
     * @param query 参
     * @param loginUser 用户
     * @return ConversationArchiveVO
     */
    PageInfo<ConversationArchiveVO> getChatAllList(ConversationArchiveQuery query, LoginUser loginUser);
}

