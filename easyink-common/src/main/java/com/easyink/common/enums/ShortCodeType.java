package com.easyink.common.enums;

import com.easyink.common.enums.wechatopen.WechatOpenEnum;
import com.easyink.common.shorturl.model.EmpleCodeShortUrlAppendInfo;
import com.easyink.common.shorturl.model.FormShortUrlAppendInfo;
import com.easyink.common.shorturl.model.GroupCodeShortUrlAppendInfo;
import com.easyink.common.shorturl.model.RadarShortUrlAppendInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 短链类型
 *
 * @author wx
 * 2023/1/15 16:51
 **/
@AllArgsConstructor
public enum ShortCodeType {

    RADAR(WechatOpenEnum.UseOfficeAccountType.RADAR.getCode(), "雷达", RadarShortUrlAppendInfo.class),
    FORM(WechatOpenEnum.UseOfficeAccountType.FORM.getCode(), "表单", FormShortUrlAppendInfo.class),
    EMPLE_CODE(WechatOpenEnum.UseOfficeAccountType.EMPLE_CODE.getCode(), "员工活码", EmpleCodeShortUrlAppendInfo.class),
    GROUP_CODE(WechatOpenEnum.UseOfficeAccountType.GROUP_CODE.getCode(), "群活码", GroupCodeShortUrlAppendInfo.class),
    ;

    @Getter
    private Integer code;
    @Getter
    private String desc;
    @Getter
    private Class<?> clazz;

    /**
     * 通过code 查找 ShortCodeType
     *
     * @param code
     * @return
     */
    public static ShortCodeType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values()).filter(it -> it.getCode().equals(code)).findFirst().orElseGet(null);
    }

}
