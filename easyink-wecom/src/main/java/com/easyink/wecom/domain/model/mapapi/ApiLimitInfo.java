package com.easyink.wecom.domain.model.mapapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * api限制详情json
 *
 * @author tigger
 * 2025/5/8 23:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiLimitInfo {
    /**
     * apicode
     */
    private Integer apiCode;

    /**
     * 每日调用限制次数
     */
    private Integer dailyLimit;
}
