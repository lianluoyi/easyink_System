package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerMessgaeResult;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeCustomerMessagePushResultDTO;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.easyink.wecom.domain.vo.WeCustomerMessageResultVO;

import java.util.List;

/**
 * 群发消息  微信消息发送结果表 we_customer_messgaeResult
 *
 * @author admin
 * @date 2020-12-12
 */
public interface WeCustomerMessgaeResultService extends IService<WeCustomerMessgaeResult> {

    /**
     * 查询微信消息发送情况
     *
     * @param weCustomerMessagePushResultDTO 查询条件
     * @return {@link WeCustomerMessageResultVO}s
     */
    List<WeCustomerMessageResultVO> customerMessagePushs(WeCustomerMessagePushResultDTO weCustomerMessagePushResultDTO);

    /**
     * 保存映射关系
     *
     * @param customerMessagePushDTO
     * @param messageId
     * @return
     */
    int workerMappingCustomer(CustomerMessagePushDTO customerMessagePushDTO, long messageId, List<WeCustomer> customers, List<WeGroup> groups);

    /**
     * 删除
     *
     * @param messageId 消息id
     * @return 受影响行数
     */
    int delete(Long messageId, String corpId);

}
