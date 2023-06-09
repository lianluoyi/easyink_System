package com.easyink.wecom.domain.dto.app;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: WeSuiteTokenReq
 *
 * @author: 1*+
 * @date: 2021-09-08 17:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeSuiteTokenReq implements Serializable {

    /**
     * 以ww或wx开头应用id（对应于旧的以tj开头的套件id）
     */
    @SerializedName("suite_id")
    private String suite_id;
    /**
     * 应用secret
     */
    @SerializedName("suite_secret")
    private String suite_secret;
    /**
     * 企业微信后台推送的ticket
     */
    @SerializedName("suite_ticket")
    private String suite_ticket;


}
