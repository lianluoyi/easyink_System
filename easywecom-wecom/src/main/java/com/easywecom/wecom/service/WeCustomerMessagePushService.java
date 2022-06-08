package com.easywecom.wecom.service;


import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.wecom.domain.dto.WeCustomerMessageDTO;
import com.easywecom.wecom.domain.dto.WeCustomerMessageToUserDTO;
import com.easywecom.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.easywecom.wecom.domain.vo.CustomerMessagePushVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.validation.annotation.Validated;

import java.text.ParseException;
import java.util.List;

/**
 * 类名： 群发消息服务类
 *
 * @author 佚名
 * @date 2021/11/17 10:11
 */
public interface WeCustomerMessagePushService {

    /**
     * 新增群发消息发送
     * @param loginUser 登录用户
     * @param customerMessagePush 原始数据信息
     * @throws JsonProcessingException  JsonProcessingException
     * @throws ParseException ParseException
     * @throws CloneNotSupportedException CloneNotSupportedException
     */
    void addWeCustomerMessagePush(CustomerMessagePushDTO customerMessagePush, LoginUser loginUser) throws JsonProcessingException, ParseException, CloneNotSupportedException;

    /**
     * 获取发送对象数量
     *
     * @param customerMessagePushDTO 发送条件
     * @param loginUser              登录用户
     * @return 发送数量
     */
    int getSendSize(CustomerMessagePushDTO customerMessagePushDTO, LoginUser loginUser);

    /**
     * 群发消息列表
     *
     * @param weCustomerMessageDTO   查询条件
     * @return {@link CustomerMessagePushVO}s
     */
    List<CustomerMessagePushVO> customerMessagePushs(WeCustomerMessageDTO weCustomerMessageDTO);

    /**
     * 修改定时任务
     * @param customerMessagePushDTO 定时群发消息
     * @param loginUser 登录用户
     * @throws ParseException ParseException
     */
    void updateTimeTask(CustomerMessagePushDTO customerMessagePushDTO, LoginUser loginUser) throws ParseException;

    /**
     * 获取定时任务信息
     * @param messageId 消息
     * @return {@link CustomerMessagePushDTO}
     */
    CustomerMessagePushDTO getMessageInfo(Long messageId);

    /**
     * 获取需要复制的信息
     *
     * @param messageId 消息
     * @return {@link CustomerMessagePushDTO}
     */
    CustomerMessagePushDTO getCopyInfo(Long messageId);

    /**
     * 删除
     *
     * @param messageId 消息id
     */
    void delete(Long messageId, String corpId);

    /**
     * 发提醒消息给员工
     *
     * @param weCustomerMessageToUserDTO 信息
     */
    void sendToUser(@Validated WeCustomerMessageToUserDTO weCustomerMessageToUserDTO);

}
