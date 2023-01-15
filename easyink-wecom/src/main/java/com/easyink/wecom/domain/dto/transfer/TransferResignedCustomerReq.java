package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeExternalContactClient;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 分配离职成员客户请求实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 16:25
 */
@Data
@Builder
public class TransferResignedCustomerReq {
    /**
     * 原跟进成员的userid
     */
    private String handover_userid;
    /**
     * 接替成员的userid
     */
    private String takeover_userid;
    /**
     * 客户的external_userid列表，最多一次转移100个客户
     */
    private List<String> external_userid;
    /**
     * 单次最大转接客户数
     */
    private final static Integer MAX_TRANSFER_NUM = 100;

    /**
     * 执行请求,如果超过100个分配客户，则分批次执行
     *
     * @param corpId 企业ID
     * @return {@link TransferResignedCustomerResp}
     */
    public TransferResignedCustomerResp execute(String corpId) {
        if (CollectionUtils.isEmpty(external_userid)) {
            return TransferResignedCustomerResp.emptyResult();
        }
        WeExternalContactClient client = SpringUtils.getBean(WeExternalContactClient.class);
        // 由于一次最多可转接100个客户 这里分隔客户列表后再分批请求
        List<List<String>> partitionList = Lists.partition(external_userid, MAX_TRANSFER_NUM);
        // 保存全部的结果
        TransferResignedCustomerResp respAll = TransferResignedCustomerResp.emptyResult();
        List<TransferResignedCustomerResp.TransferResult> totalList = new ArrayList<>();
        // 每批次返回数据
        TransferResignedCustomerResp onceResp;
        // 至少有一次正确返回
        boolean resultSuccessFlag = false;
        for (List<String> subList : partitionList) {
            if (CollectionUtils.isEmpty(subList)) {
                continue;
            }
            this.external_userid = subList;
            onceResp = client.transferResignedCustomer(this, corpId);
            if (onceResp != null && CollectionUtils.isNotEmpty(onceResp.getCustomer())) {
                // 其他参数只复制一次
                if(!resultSuccessFlag){
                    BeanUtils.copyBeanProp(respAll, onceResp);
                    resultSuccessFlag = true;
                }
                totalList.addAll(onceResp.getCustomer());
            }
        }
        respAll.setCustomer(totalList);
        return respAll;
    }




}
