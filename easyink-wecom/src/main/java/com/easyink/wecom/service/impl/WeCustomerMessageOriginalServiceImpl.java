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
import com.easyink.wecom.domain.WeCustomerMessage;
import com.easyink.wecom.domain.WeCustomerMessageOriginal;
import com.easyink.wecom.domain.WeCustomerMessgaeResult;
import com.easyink.wecom.domain.dto.WeCustomerMessageDTO;
import com.easyink.wecom.domain.dto.message.*;
import com.easyink.wecom.domain.vo.CustomerMessagePushVO;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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


    @DataScope
    @Override
    public List<CustomerMessagePushVO> customerMessagePushs(WeCustomerMessageDTO weCustomerMessageDTO) {
        return weCustomerMessageOriginalMapper.selectCustomerMessagePushs(weCustomerMessageDTO);
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
    public void asyncResult(AsyncResultDTO asyncResultDTO, String corpId) {
        syncSendResult(asyncResultDTO.getMsgids(), asyncResultDTO.getMessageId(), corpId);
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

    private void syncSendResult(List<String> msgIds, Long messageId, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new WeComException("corpId不能为空");
        }
        AtomicInteger atomicInteger = new AtomicInteger();
        if (CollectionUtils.isNotEmpty(msgIds)) {
            msgIds.forEach(msgId -> {
                QueryCustomerMessageStatusResultDataObjectDTO dataObjectDto = new QueryCustomerMessageStatusResultDataObjectDTO();
                dataObjectDto.setMsgid(msgId);
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
                        }
                        weCustomerMessgaeResultMapper.updateWeCustomerMessgaeResult(messageId, detail.getChat_id(), detail.getExternal_userid(), detail.getStatus(), detail.getSend_time(), detail.getUserid(), remark);
                        remark = StringUtils.EMPTY;
                    }
                    //把同个messageid 只发送其中几条，未发送的群修改状态
                    for (DetailMessageStatusResultDTO detailMessageStatusResultDTO : detailList) {
                        if (StringUtils.isNotBlank(detailMessageStatusResultDTO.getChat_id()) && MessageStatusEnum.SEND_SUCCEED.getType().equals(detailMessageStatusResultDTO.getStatus())) {
                            weCustomerMessgaeResultMapper.update(null, new LambdaUpdateWrapper<WeCustomerMessgaeResult>()
                                    .eq(WeCustomerMessgaeResult::getMessageId, messageId)
                                    .eq(WeCustomerMessgaeResult::getStatus, MessageStatusEnum.NOT_SEND.getType())
                                    .eq(WeCustomerMessgaeResult::getUserid, detailMessageStatusResultDTO.getUserid())
                                    .set(WeCustomerMessgaeResult::getStatus, MessageStatusEnum.NOT_SEND_GROUP.getType())
                                    .set(WeCustomerMessgaeResult::getRemark, MessageStatusEnum.NOT_SEND_GROUP.getName()));
                        }
                    }
                }
            });
        }
        //更新微信实际发送条数
        weCustomerMessageService.updateWeCustomerMessageActualSend(messageId, atomicInteger.get());
    }

}
