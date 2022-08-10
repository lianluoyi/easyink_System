package com.easyink.wecom.strategy;

import com.easyink.wecom.domain.WeMessagePush;

public class MessageContext {

    private Strategy strategy;

    public MessageContext(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 推送消息
     *
     * @param weMessagePush 消息发送的
     */
    public void sendMessage(WeMessagePush weMessagePush, String corpId) {
        strategy.sendMessage(weMessagePush, corpId);
    }

}
