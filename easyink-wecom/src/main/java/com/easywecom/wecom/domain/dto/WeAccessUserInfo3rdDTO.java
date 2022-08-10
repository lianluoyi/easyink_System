package com.easywecom.wecom.domain.dto;

import lombok.Data;

/**
 * 获取访问用户身份
 *
 * @author Society my sister Li
 * @date 2021/9/26
 */
@Data
public class WeAccessUserInfo3rdDTO extends WeResultDTO{

    private String CorpId;

    private String UserId;

    private String DeviceId;

    private String user_ticket;

    private Integer expires_in;

    private String open_userid;
}
