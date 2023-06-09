package com.easyink.common.shorturl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 短链附加信息
 *
 * @author wx
 * 2023/1/15 18:24
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseShortUrlAppendInfo {
    /**
     * 使用短链的员工id
     */
    private String userId;

    /**
     * 企业id
     */
    private String corpId;

}
