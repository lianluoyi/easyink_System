package com.easywecom.wecom.domain.dto.transfer;

import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.client.WeExternalContactClient;
import com.easywecom.wecom.domain.dto.WePageBaseResp;

/**
 * 类名: 离职接替状态获取请求实体
 *
 * @author : silver_chariot
 * @date : 2021/12/8 9:19
 */
public class TransferResultResignedReq extends TransferResultReq {


    public TransferResultResignedReq(String handover_userid, String takeover_userid) {
        super(handover_userid, takeover_userid);
    }

    @Override
    public WePageBaseResp<TransferResultResp.ResultDetail> execute(String corpId) {
        WeExternalContactClient client = SpringUtils.getBean(WeExternalContactClient.class);
        return client.transferResignedResult(this, corpId);
    }
}
