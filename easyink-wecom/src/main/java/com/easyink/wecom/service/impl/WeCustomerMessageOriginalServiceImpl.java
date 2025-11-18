package com.easyink.wecom.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.MessageStatusEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeCustomerMessagePushClient;
import com.easyink.wecom.domain.WeCustomerMessageOriginal;
import com.easyink.wecom.domain.WeCustomerMessgaeResult;
import com.easyink.wecom.domain.dto.WeCustomerMessageDTO;
import com.easyink.wecom.domain.dto.message.*;
import com.easyink.wecom.domain.vo.CustomerMessagePushVO;
import com.easyink.wecom.domain.vo.WeCustomerSeedMessageVO;
import com.easyink.wecom.mapper.WeCustomerMessageMapper;
import com.easyink.wecom.mapper.WeCustomerMessageOriginalMapper;
import com.easyink.wecom.mapper.WeCustomerMessgaeResultMapper;
import com.easyink.wecom.service.WeCustomerMessageOriginalService;
import com.easyink.wecom.service.WeCustomerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 群发消息 原始数据信息表 we_customer_messageOriginal
 *
 * @author admin
 * @date 2020-12-12
 */
@Service
@Slf4j
public class WeCustomerMessageOriginalServiceImpl extends ServiceImpl<WeCustomerMessageOriginalMapper, WeCustomerMessageOriginal> implements WeCustomerMessageOriginalService {

    @Autowired
    private WeCustomerMessageOriginalMapper weCustomerMessageOriginalMapper;

    @Autowired
    private WeCustomerMessagePushClient weCustomerMessagePushClient;

    @Autowired
    private WeCustomerMessgaeResultMapper weCustomerMessgaeResultMapper;

    @Autowired
    private WeCustomerMessageService weCustomerMessageService;
    @Autowired
    private WeCustomerMessageMapper weCustomerMessageMapper;


    @DataScope
    @Override
    public List<CustomerMessagePushVO> customerMessagePushs(WeCustomerMessageDTO weCustomerMessageDTO) {
        List<CustomerMessagePushVO> customerMessagePushVOS = weCustomerMessageOriginalMapper.selectCustomerMessagePushs(weCustomerMessageDTO);
        // 补充 seedMessageList（expectSend 和 actualSend 已在 SQL 中聚合）
        fillMessageInfo(customerMessagePushVOS);
        return customerMessagePushVOS;
    }

    /**
     * 补充群发详情（子消息列表）
     * @param customerMessagePushVOS
     */
    private void fillMessageInfo(List<CustomerMessagePushVO> customerMessagePushVOS) {
        if (CollectionUtils.isEmpty(customerMessagePushVOS)) {
            return;
        }
        
        // 收集所有 messageId（从 messageIdDTOList 中获取第一个批次的 messageId 即可，因为所有批次共享相同的附件）
        List<Long> messageIdList = customerMessagePushVOS.stream()
                .filter(vo -> CollectionUtils.isNotEmpty(vo.getMessageIdDTOList()))
                .map(vo -> vo.getMessageIdDTOList().get(0).getMessageId())
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(messageIdList)) {
            return;
        }
        
        // 查询子消息列表
        Map<Long, List<WeCustomerSeedMessageVO>> messageSeedResult = weCustomerMessageMapper.selectMessageListByMessageIdList(messageIdList)
                .stream().collect(
                        Collectors.groupingBy(WeCustomerSeedMessageVO::getMessageId));
        
        // 补充子消息列表（所有批次共享相同的子消息，只需要查询一次）
        for (CustomerMessagePushVO customerMessagePushVO : customerMessagePushVOS) {
            if (CollectionUtils.isEmpty(customerMessagePushVO.getMessageIdDTOList())) {
                continue;
            }
            // 取第一个批次的 messageId 查询子消息
            Long firstMessageId = Long.valueOf(customerMessagePushVO.getMessageIdDTOList().get(0).getMessageId());
            customerMessagePushVO.setSeedMessageList(messageSeedResult.getOrDefault(firstMessageId, new ArrayList<>()));
        }
    }

    @Override
    public CustomerMessagePushVO customerMessagePushDetail(Long messageId, String corpId) {
        //为空则返回空对象
        if (StringUtils.isBlank(corpId)) {
            return new CustomerMessagePushVO();
        }
        CustomerMessagePushVO customerMessagePushDetail = weCustomerMessageOriginalMapper.findCustomerMessagePushDetail(messageId, corpId);
        return customerMessagePushDetail;
    }

    @Override
    public void asyncResult(List<MessageIdDTO> messageIdDTOList, String corpId) {
        syncSendResult(messageIdDTOList, corpId);
    }

    @Override
    public long saveWeCustomerMessageOriginal(CustomerMessagePushDTO customerMessagePushDTO, String staffId) {
        //保存原始数据信息表 WeCustomerMessageOriginal 主键id
        long messageOriginalId = SnowFlakeUtil.nextId();
        WeCustomerMessageOriginal original = new WeCustomerMessageOriginal();
        original.setMessageOriginalId(messageOriginalId);
        original.setDelFlag(0);
        BeanUtils.copyProperties(customerMessagePushDTO, original);
        original.setStaffId(StrUtil.emptyIfNull(staffId));
        this.save(original);
        return messageOriginalId;
    }

    private void syncSendResult(List<MessageIdDTO> msgDTOList, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new WeComException("corpId不能为空");
        }
        if (CollectionUtils.isNotEmpty(msgDTOList)) {
            msgDTOList.forEach(msgDTO -> {
                AtomicInteger atomicInteger = new AtomicInteger();
                QueryCustomerMessageStatusResultDataObjectDTO dataObjectDto = new QueryCustomerMessageStatusResultDataObjectDTO();
                dataObjectDto.setMsgid(msgDTO.getMsgId());
                //拉取发送结果
                QueryCustomerMessageStatusResultDTO queryCustomerMessageStatusResultDTO = weCustomerMessagePushClient.queryCustomerMessageStatus(dataObjectDto, corpId);
                if (WeExceptionTip.WE_EXCEPTION_TIP_41063.getCode().equals(queryCustomerMessageStatusResultDTO.getErrcode())) {
                    throw new CustomException(ResultTip.TIP_SENDING_MESSAGE);
                }
                if (WeConstans.WE_SUCCESS_CODE.equals(queryCustomerMessageStatusResultDTO.getErrcode())) {
                    List<DetailMessageStatusResultDTO> detailList = queryCustomerMessageStatusResultDTO.getDetail_list();
                    String remark = StringUtils.EMPTY;
                    for (DetailMessageStatusResultDTO detail : detailList) {
                        if (MessageStatusEnum.NOT_FRIEND.getType().equals(detail.getStatus())) {
                            remark = MessageStatusEnum.NOT_FRIEND.getName();
                        } else if (MessageStatusEnum.ALREADY_SEND.getType().equals(detail.getStatus())) {
                            remark = MessageStatusEnum.ALREADY_SEND.getName();
                        }else if(MessageStatusEnum.SEND_SUCCEED.getType().equals(detail.getStatus())){
                            atomicInteger.addAndGet(1);
                        }
                        weCustomerMessgaeResultMapper.updateWeCustomerMessgaeResult(Long.valueOf(msgDTO.getMessageId()), detail.getChat_id(), detail.getExternal_userid(), detail.getStatus(), detail.getSend_time(), detail.getUserid(), remark);
                        remark = StringUtils.EMPTY;
                    }
                    //把同个messageid 只发送其中几条，未发送的群修改状态
                    for (DetailMessageStatusResultDTO detailMessageStatusResultDTO : detailList) {
                        if (StringUtils.isNotBlank(detailMessageStatusResultDTO.getChat_id()) && MessageStatusEnum.SEND_SUCCEED.getType().equals(detailMessageStatusResultDTO.getStatus())) {
                            weCustomerMessgaeResultMapper.update(null, new LambdaUpdateWrapper<WeCustomerMessgaeResult>()
                                    .eq(WeCustomerMessgaeResult::getMessageId, msgDTO.getMessageId())
                                    .eq(WeCustomerMessgaeResult::getStatus, MessageStatusEnum.NOT_SEND.getType())
                                    .eq(WeCustomerMessgaeResult::getUserid, detailMessageStatusResultDTO.getUserid())
                                    .set(WeCustomerMessgaeResult::getStatus, MessageStatusEnum.NOT_SEND_GROUP.getType())
                                    .set(WeCustomerMessgaeResult::getRemark, MessageStatusEnum.NOT_SEND_GROUP.getName()));
                        }
                    }
                }
                //更新微信实际发送条数
                weCustomerMessageService.updateWeCustomerMessageActualSend(Long.valueOf(msgDTO.getMessageId()), atomicInteger.get());
            });
        }

    }

}
