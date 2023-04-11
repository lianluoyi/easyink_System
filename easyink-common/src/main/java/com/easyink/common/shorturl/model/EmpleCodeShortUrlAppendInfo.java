package com.easyink.common.shorturl.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 类名: 活码小程序短链附加信息
 *
 * @author : silver_chariot
 * @date : 2023/3/13 15:59
 **/
@Data
@SuperBuilder
@NoArgsConstructor
public class EmpleCodeShortUrlAppendInfo extends BaseShortUrlAppendInfo {

    /**
     * 活码Id
     */
    private Long id ;
}
