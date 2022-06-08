package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeCustomerSeedMessage;
import com.easywecom.wecom.domain.dto.message.CustomerMessagePushDTO;

/**
 * 群发消息  子消息表(包括 文本消息、图片消息、链接消息、小程序消息) we_customer_seedMessage
 *
 * @author admin
 * @date 2020-12-28
 */
public interface WeCustomerSeedMessageService extends IService<WeCustomerSeedMessage> {
    /**
     * 各分类消息表
     *
     * @param customerMessagePushDTO 群发消息
     * @param messageId              消息id
     */
    void saveSeedMessage(CustomerMessagePushDTO customerMessagePushDTO, long messageId);

    /**
     * 删除
     *
     * @param messageId 消息id
     * @param corpId
     * @return 受影响行数
     */
    int deleteByMessageId(Long messageId, String corpId);
}
