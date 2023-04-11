package com.easyink.common.shorturl.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 类名: 群活码短链附加信息
 *
 * @author : silver_chariot
 * @date : 2023/3/14 16:58
 **/
@Data
@NoArgsConstructor
@SuperBuilder
public class GroupCodeShortUrlAppendInfo extends BaseShortUrlAppendInfo {

    /**
     * 群活码id
     */
    private Long groupCodeId;
}
