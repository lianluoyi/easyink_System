package com.easywecom.wecom.domain.dto;

import lombok.Data;

/**
 * @description: accessToken相关
 * @author admin
 * @create: 2020-08-27 15:54
 **/
@Data
public class WeAccessTokenDTO extends WeResultDTO {
    private String access_token;
    private Long expires_in;
    private String provider_access_token;
}
