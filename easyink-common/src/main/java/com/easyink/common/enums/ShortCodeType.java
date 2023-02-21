package com.easyink.common.enums;

import com.easyink.common.enums.wechatopen.WechatOpenEnum;
import com.easyink.common.shorturl.FormShortUrlAppendInfo;
import com.easyink.common.shorturl.RadarShortUrlAppendInfo;
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
