package com.easywecom.wecom.domain.dto.app;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WePreAuthCodeResp
 *
 * @author: 1*+
 * @date: 2021-09-08 16:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WePreAuthCodeResp extends WeResultDTO {

    /**
     * 预授权码,最长为512字节
     */
    private String preAuthCode;
    /**
     * 有效期（秒）
     */
    private Integer expiresIn;

}
