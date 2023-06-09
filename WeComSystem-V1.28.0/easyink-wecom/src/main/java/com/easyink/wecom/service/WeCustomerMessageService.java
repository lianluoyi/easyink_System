package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerMessage;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * 群发消息  微信消息表service接口
 *
 * @author admin
 * @date 2020-12-12
 */
public interface WeCustomerMessageService extends IService<WeCustomerMessage> {

    /**
     * @param messageId  微信消息表主键id
     * @param actualSend 实际发送消息数（客户对应多少人 客户群对应多个群）
     * @return int
     */
    int updateWeCustomerMessageActualSend(Long messageId, Integer actualSend);

    /**
     * 保存微信消息  WeCustomerMessage
     *
     * @param loginUser 登录用户
     * @param messageId
     * @param messageOriginalId
     * @param customerMessagePushDTO
     * @param size
     */
    void saveWeCustomerMessage(LoginUser loginUser, long messageId, long messageOriginalId, CustomerMessagePushDTO customerMessagePushDTO, int size);

    /**
     * @param messageId
     * @param msgIds
     * @throws JsonProcessingException
     */
    void updateMsgId(long messageId, List<String> msgIds) throws JsonProcessingException;

    /**
     * 发送消息(定时任务调度)
     *
     * @param customerMessagePushDTO 消息信息
     * @param messageId 消息id
     * @param customers 客户
     * @throws JsonProcessingException JsonProcessingException
     */
    void sendMessage(CustomerMessagePushDTO customerMessagePushDTO, Long messageId, List<WeCustomer> customers) throws JsonProcessingException;

    /**
     * 删除
     *
     * @param messageId 消息id
     * @param corpId
     * @return 受影响行数
     */
    int deleteByMessageId(Long messageId, String corpId);

    /**
     * 获得朋友圈附件id（企业朋友圈使用）
     *
     * @param url    链接
     * @param type   类型
     * @param name   名称
     * @param corpId 企业id
     * @return 附件id
     */
    String buildMediaId(String url, String type, Integer attachmentType, String name, String corpId);

    /**
     * 获得临时素材id（朋友圈客户端可使用）
     * @param url    链接
     * @param type   类型
     * @param name   名称
     * @param corpId 企业id
     * @return 临时素材id
     */
    String buildMediaId(String url, String type, String name, String corpId);

}
