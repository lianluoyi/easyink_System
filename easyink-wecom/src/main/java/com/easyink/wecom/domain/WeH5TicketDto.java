package com.easyink.wecom.domain;

import com.easyink.wecom.domain.dto.WeResultDTO;
import lombok.Data;

/**
 * @author admin
 * @description h5签名
 * @date 2020/12/3 10:26
 **/
@Data
public class WeH5TicketDto extends WeResultDTO {
    /**
     * 生成签名所需的jsapi_ticket，最长为512字节
     */
    private String ticket;
    /**
     * 凭证的有效时间（秒）
     */
    private Integer expiresIn;
}
