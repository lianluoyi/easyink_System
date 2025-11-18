package com.easyink.common.shorturl;

import com.easyink.common.shorturl.model.BaseShortUrlAppendInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 类名: 客户专属活码短链附加信息
 *
 * @author : tigger
 * @date : 2025/1/13 10:59
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class CustomerEmpleCodeShortUrlAppendInfo extends BaseShortUrlAppendInfo {

    /**
     * 活码Id
     */
    private Long id;
}
