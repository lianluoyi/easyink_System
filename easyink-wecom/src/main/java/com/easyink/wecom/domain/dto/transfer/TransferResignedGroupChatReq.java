package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeExternalContactClient;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: 分配离职成员的客户群请求实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 19:45
 */
@Data
@Builder
public class TransferResignedGroupChatReq {
    /**
     * 需要转群主的客户群ID列表。取值范围： 1 ~ 100
     */
    private List<String> chat_id_list;
    /**
     * 新群主ID
     */
    private String new_owner;

    private final static Integer MAX_TRANSFER_NUM = 100;

    /**
     * 执行请求,如果超过100个分配客户，则分批次执行
     *
     * @param corpId 企业ID
     * @return {@link TransferResignedCustomerResp}
     */
    public TransferResignedGroupChatResp execute(String corpId) {
        if (CollectionUtils.isEmpty(chat_id_list)) {
            return TransferResignedGroupChatResp.getEmptyResult();
        }
        WeExternalContactClient client = SpringUtils.getBean(WeExternalContactClient.class);
        // 一次最懂分配100个客户群,此处分批请求
        List<List<String>> partitionList = Lists.partition(chat_id_list, MAX_TRANSFER_NUM);
        List<TransferResignedGroupChatResp.FailResult> totalList = new ArrayList<>();
        TransferResignedGroupChatResp resp = new TransferResignedGroupChatResp();
        for (List<String> subList : partitionList) {
            if (CollectionUtils.isEmpty(subList)) {
                continue;
            }
            this.chat_id_list = subList;
            resp = client.transferResignedGroup(this, corpId);
            if (resp != null && CollectionUtils.isNotEmpty(resp.getFailed_chat_list())) {
                totalList.addAll(resp.getFailed_chat_list());
            }
        }
        resp.setFailed_chat_list(totalList);
        return resp;
    }


}
