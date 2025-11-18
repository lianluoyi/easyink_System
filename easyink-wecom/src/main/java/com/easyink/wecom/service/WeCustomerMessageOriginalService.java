package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeCustomerMessageOriginal;
import com.easyink.wecom.domain.dto.WeCustomerMessageDTO;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.easyink.wecom.domain.dto.message.MessageIdDTO;
import com.easyink.wecom.domain.vo.CustomerMessagePushVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * 群发消息 原始数据信息表 we_customer_messageOriginal
 *
 * @author admin
 * @date 2020-12-12
 */
public interface WeCustomerMessageOriginalService extends IService<WeCustomerMessageOriginal> {


    /**
     * 群发消息列表
     *
     * @param weCustomerMessageDTO   查询条件
     * @return {@link CustomerMessagePushVO}s
     */
    List<CustomerMessagePushVO> customerMessagePushs(WeCustomerMessageDTO weCustomerMessageDTO);

    /**
     * 群发详情
     *
     * @param messageId 微信群发id
     * @param corpId 企业id
     * @return {@link CustomerMessagePushVO} 群发详情
     */
    CustomerMessagePushVO customerMessagePushDetail(Long messageId, String corpId);


    /**
     * 同步发送结果
     *
     * @param asyncResultDTO 可以用于获取发送结果
     * @param corpId 企业id
     * @throws JsonProcessingException JsonProcessingException
     */
    void asyncResult(List<MessageIdDTO> messageIdDTOList, String corpId) throws JsonProcessingException;

    /**
     * 保存原始数据信息表 WeCustomerMessageOriginal 主键id
     *
     * @param customerMessagePushDTO
     * @param staffId                   前端传入的staffId
     * @return
     */
    long saveWeCustomerMessageOriginal(CustomerMessagePushDTO customerMessagePushDTO,String staffId);

}
