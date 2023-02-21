package com.easyink.common.enums.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 小程序或公众号的主体类型
 * 小程序或公众号的主体类型：0表示主体名称是企业的 (默认)，1表示主体名称是服务商的
 *
 * @author wx
 * 2023/2/9 20:06
 **/
@AllArgsConstructor
public enum SubjectTypeEnum {
    ENTERPRISE(0),
    SERVICE_PROVIDER(1),
    ;
    @Getter
    private Integer code;
}
