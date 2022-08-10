package com.easyink.wecom.domain.resp;

import com.easyink.wecom.domain.dto.WeResultDTO;
import lombok.Data;

/**
 * 类名: unionid转客户id响应
 *
 * @author : silver_chariot
 * @date : 2022/7/21 16:38
 **/
@Data
public class UnionId2ExternalUserIdResp extends WeResultDTO {

    /**
     * 该企业的外部联系人ID
     */
    private String external_userid;
}
