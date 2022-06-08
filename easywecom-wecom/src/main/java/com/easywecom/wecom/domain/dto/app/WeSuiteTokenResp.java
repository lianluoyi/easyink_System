package com.easywecom.wecom.domain.dto.app;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WeSuiteTokenResp
 *
 * @author: 1*+
 * @date: 2021-09-08 17:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WeSuiteTokenResp extends WeResultDTO {

    /**
     * 第三方应用access_token,最长为512字节
     */
    private String suiteAccessToken;

    /**
     * 有效期（秒）
     */
    private Long expiresIn;

}
