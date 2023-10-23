package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerMessgaeResult;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeCustomerMessagePushResultDTO;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.easyink.wecom.domain.vo.WeCustomerMessageResultVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerMessgaeResultMapper;
import com.easyink.wecom.service.WeCustomerMessgaeResultService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 群发消息  微信消息发送结果表 we_customer_messgaeResult
 *
 * @author admin
 * @date 2020-12-12
 */
@Service
public class WeCustomerMessgaeResultServiceImpl extends ServiceImpl<WeCustomerMessgaeResultMapper, WeCustomerMessgaeResult> implements WeCustomerMessgaeResultService {

    @Autowired
    private WeCustomerMessgaeResultMapper customerMessgaeResultMapper;

    /**
     * 查询微信消息发送情况
     *
     * @param weCustomerMessagePushResultDTO 查询条件
     * @return {@link WeCustomerMessageResultVO}s
     */
    @Override
    public List<WeCustomerMessageResultVO> customerMessagePushs(WeCustomerMessagePushResultDTO weCustomerMessagePushResultDTO) {
        if (StringUtils.isEmpty(weCustomerMessagePushResultDTO.getCorpId())) {
            return new ArrayList<>();
        }
        if (WeConstans.NOT_SEND.equals(weCustomerMessagePushResultDTO.getSendStatus())) {
            List<WeCustomerMessageResultVO> resultList = customerMessgaeResultMapper.customerMessagePushs(weCustomerMessagePushResultDTO);
            if (CollectionUtils.isEmpty(resultList)) {
                return Collections.emptyList();
            }
            List<WeCustomerMessageResultVO> customerList = customerMessgaeResultMapper.messagePushsByCustomer(weCustomerMessagePushResultDTO);
            if (CollectionUtils.isEmpty(customerList)) {
                return Collections.emptyList();
            }
            resultList.forEach(item -> {
                // 筛选出员工名称相同的信息
                List<String> sendCustomerNameList = customerList.stream().filter(i -> i.getUserName().equals(item.getUserName()))
                                                                         .map(WeCustomerMessageResultVO::getCustomers)
                                                                         .collect(Collectors.toList());
                // 将发送的客户/客户群名称转换成以逗号分隔的字符串
                String customers = String.join(Constants.CUSTOMER_PUSH_MESSAGE_SEPARATOR, sendCustomerNameList);
                item.setCustomers(customers);
            });
            return resultList;
        } else {
            return customerMessgaeResultMapper.listOfMessageResult(weCustomerMessagePushResultDTO);
        }
    }

    @Override
    public int workerMappingCustomer(CustomerMessagePushDTO customerMessagePushDTO, long messageId, List<WeCustomer> customers, List<WeGroup> groups) {

        int size = 0;
        // 0 发给客户
        if (WeConstans.SEND_MESSAGE_CUSTOMER.equals(customerMessagePushDTO.getPushType()) && CollectionUtils.isNotEmpty(customers)) {
            List<WeCustomerMessgaeResult> weCustomerMessgaeResults = Lists.newArrayList();

            size = customers.size();
            customers.forEach(customer -> {
                //微信消息发送结果、保存员工客户关系映射关系数据
                WeCustomerMessgaeResult customerMessgaeResult = new WeCustomerMessgaeResult();
                customerMessgaeResult.setMessgaeResultId(SnowFlakeUtil.nextId());
                customerMessgaeResult.setMessageId(messageId);
                customerMessgaeResult.setExternalUserid(customer.getExternalUserid());
                customerMessgaeResult.setUserid(customer.getUserId());
                customerMessgaeResult.setStatus(WeConstans.sendMessageStatusEnum.NOT_SEND.getStatus());
                //这个是实际发送时间
                customerMessgaeResult.setExternalName(customer.getName());
                customerMessgaeResult.setUserName(customer.getUserName());
                customerMessgaeResult.setSendType(customerMessgaeResult.getSettingTime() == null ? customerMessagePushDTO.getPushType() : WeConstans.SEND_MESSAGE_JOB);
                customerMessgaeResult.setSettingTime(customerMessagePushDTO.getSettingTime());
                customerMessgaeResult.setDelFlag(WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE);
                customerMessgaeResult.setCreateBy(LoginTokenService.getUsername());
                customerMessgaeResult.setUpdateBy(LoginTokenService.getUsername());

                weCustomerMessgaeResults.add(customerMessgaeResult);
            });
            customerMessgaeResultMapper.batchInsert(weCustomerMessgaeResults);

        }

        // 1 发给客户群
        if (WeConstans.SEND_MESSAGE_GROUP.equals(customerMessagePushDTO.getPushType()) && CollectionUtils.isNotEmpty(groups)) {
            List<WeCustomerMessgaeResult> weCustomerMessgaeResults = Lists.newArrayList();
            size = groups.size();
            groups.forEach(group -> {
                //微信消息发送结果、保存员工客户关系映射关系数据
                WeCustomerMessgaeResult customerMessgaeResult = new WeCustomerMessgaeResult();
                customerMessgaeResult.setMessgaeResultId(SnowFlakeUtil.nextId());
                customerMessgaeResult.setMessageId(messageId);
                customerMessgaeResult.setSendTime(customerMessagePushDTO.getSettingTime());
                customerMessgaeResult.setSendType(customerMessgaeResult.getSettingTime() == null ? customerMessagePushDTO.getPushType() : WeConstans.SEND_MESSAGE_JOB);
                customerMessgaeResult.setSettingTime(customerMessagePushDTO.getSettingTime());
                customerMessgaeResult.setChatId(group.getChatId());
                customerMessgaeResult.setChatName(group.getGroupName());
                customerMessgaeResult.setStatus(WeConstans.sendMessageStatusEnum.NOT_SEND.getStatus());
                //群管理员id
                customerMessgaeResult.setUserid(group.getOwner());
                //群管理员名称
                customerMessgaeResult.setUserName(group.getGroupLeaderName());
                customerMessgaeResult.setDelFlag(WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE);
                weCustomerMessgaeResults.add(customerMessgaeResult);
            });
            customerMessgaeResultMapper.batchInsert(weCustomerMessgaeResults);
        }
        return size;
    }

    @Override
    public int delete(Long messageId, String corpId) {
        StringUtils.checkCorpId(corpId);
        return customerMessgaeResultMapper.deleteByMessageId(messageId, corpId);
    }

}
