package com.easyink.common.shorturl.enums;

import com.easyink.common.shorturl.CustomerEmpleCodeShortUrlAppendInfo;
import com.easyink.common.shorturl.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名: 短链类型枚举
 *
 * @author : silver_chariot
 * @date : 2022/8/15 16:20
 **/
@AllArgsConstructor
@Getter
public enum ShortUrlTypeEnum {
    UNKNOWN(0, "未知", BaseShortUrlAppendInfo.class ),
    RADAR(1, "雷达", RadarShortUrlAppendInfo.class),
    FORM(2, "智能表单", FormShortUrlAppendInfo.class),
    USER_CODE(3, "员工活码", EmpleCodeShortUrlAppendInfo.class),
    GROUP_CODE(4, "群活码", GroupCodeShortUrlAppendInfo.class),
    CUSTOMER_USER_CODE(5, "客户专属活码", CustomerEmpleCodeShortUrlAppendInfo.class),
    ;
    /**
     * 类型码
     */
    private final Integer type;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 附加信息的类 ，需继承 {@link com.easyink.common.shorturl.model.BaseShortUrlAppendInfo}
     */
    private final Class appendClazz;

}
