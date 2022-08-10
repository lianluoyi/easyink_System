package com.easyink.wecom.domain.dto.transfer;

import lombok.Builder;
import lombok.Data;

/**
 * 类名: 分配在职成员客户API请求实体
 *
 * @author : silver_chariot
 * @date : 2021/11/30 9:12
 */
@Data
@Builder
public class TransferCustomerReq {
    /**
     * 原跟进成员的userid
     */
    private String handover_userid;

    /**
     * 接替成员的userid
     */
    private String takeover_userid;

    /**
     * 客户的external_userid列表
     */
    private String[] external_userid;
    /**
     * 转移成功后发给客户的消息，最多200个字符，不填则使用默认文案
     */
    private String transfer_success_msg;
}
