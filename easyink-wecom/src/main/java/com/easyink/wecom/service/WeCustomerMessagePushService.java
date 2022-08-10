package com.easyink.wecom.service;


import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.WeCustomerMessageDTO;
import com.easyink.wecom.domain.dto.WeCustomerMessageToUserDTO;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.easyink.wecom.domain.vo.CustomerMessagePushVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.validation.annotation.Validated;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 类名： 群发消息服务类
 *
 * @author 佚名
 * @date 2021/11/17 10:11
 */
public interface WeCustomerMessagePushService {


    /**
     * 客户的外部联系人id列表，仅在chat_type为single时有效，不可与sender同时为空，最多可传入1万个客户
     *
     * @param corpId     企业id
     * @param pushRange  消息范围 0 全部客户  1 指定客户
     * @param staffId    员工id
     * @param tag        客户标签id列表
     * @param filterTags 过滤用的标签
     * @param gender     性别
     * @return {@link List<WeCustomer>} 客户的外部联系人id列表
     */
    List<WeCustomer> getExternalUserIds(String corpId, String pushRange, String staffId, String departmentIds, String tag, String filterTags, Integer gender, Date startTime, Date endTime);

    /**
     * 新增群发消息发送
     *
     * @param loginUser           登录用户
     * @param customerMessagePush 原始数据信息
     * @throws JsonProcessingException    JsonProcessingException
     * @throws ParseException             ParseException
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
