package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.utils.bean.BeanUtils;
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
        // 每批次返回数据
        TransferResignedGroupChatResp onceResp;
        // 保存全部的结果
        TransferResignedGroupChatResp respAll = new TransferResignedGroupChatResp();
        // 至少有一次正确返回
        boolean resultSuccessFlag = false;
        for (List<String> subList : partitionList) {
            if (CollectionUtils.isEmpty(subList)) {
                continue;
            }
            this.chat_id_list = subList;
            onceResp = client.transferResignedGroup(this, corpId);
            if (onceResp != null && CollectionUtils.isNotEmpty(onceResp.getFailed_chat_list())) {
                // 其他参数只复制一次
                if(!resultSuccessFlag){
                    BeanUtils.copyBeanProp(respAll, onceResp);
                    resultSuccessFlag = true;
                }
                totalList.addAll(onceResp.getFailed_chat_list());
            }
        }
        respAll.setFailed_chat_list(totalList);
        return respAll;
    }


}
