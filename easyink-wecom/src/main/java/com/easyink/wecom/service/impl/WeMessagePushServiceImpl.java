package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.easyink.common.enums.PushType;
import com.easyink.wecom.domain.WeMessagePush;
import com.easyink.wecom.mapper.WeMessagePushMapper;
import com.easyink.wecom.service.WeMessagePushService;
import com.easyink.wecom.strategy.MessageContext;
import com.easyink.wecom.strategy.SendMessageToUserGroupStrategy;
import com.easyink.wecom.strategy.SendMessageToUserStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 消息发送的Service接口
 *
 * @author admin
 * @date 2020-10-28
 */
@Service
public class WeMessagePushServiceImpl implements WeMessagePushService {

    @Autowired
    private WeMessagePushMapper weMessagePushMapper;

    @Autowired
    private SendMessageToUserGroupStrategy sendMessageToUserGroupStrategy;

    @Autowired
    private SendMessageToUserStrategy sendMessageToUserStrategy;

    @Override
    public WeMessagePush selectWeMessagePushById(Long messagePushId) {
        return weMessagePushMapper.selectById(messagePushId);
    }

    @Override
    public List<WeMessagePush> selectWeMessagePushList(WeMessagePush weMessagePush) {
        return weMessagePushMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void insertWeMessagePush(WeMessagePush weMessagePush, String corpId) {

        sendMessgae(weMessagePush, corpId);

    }

    /**
     * 发送消息
     *
     * @param weMessagePush
     */
    public void sendMessgae(WeMessagePush weMessagePush, String corpId) {
        if (weMessagePush.getPushType() != null && weMessagePush.getPushType().equals(PushType.SEND_TO_USER.getType())) {
            new MessageContext(sendMessageToUserStrategy).sendMessage(weMessagePush, corpId);
        }
        if (weMessagePush.getPushType() != null
                && weMessagePush.getPushType().equals(PushType.SENT_TO_USER_GROUP.getType())) {
            new MessageContext(sendMessageToUserGroupStrategy).sendMessage(weMessagePush, corpId);
        }
    }

    @Override
    public int deleteWeMessagePushByIds(Long[] messagePushIds) {
        return weMessagePushMapper.deleteBatchIds(Arrays.asList(messagePushIds));
    }

    @Override
    public int deleteWeMessagePushById(Long messagePushId) {
        return weMessagePushMapper.deleteById(messagePushId);
    }

}
