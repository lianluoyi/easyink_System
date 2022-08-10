package com.easyink.common.enums;

import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 类名：WeEmpleCodeSkipVerifyEnum 员工活码SkipVerify枚举值说明
 *
 * @author Society my sister Li
 * @date 2021-11-02 13:50
 */
public enum WeEmployCodeSkipVerifyEnum {

    //不自动通过
    NO_PASS(0),
    //全天通过
    ALL_PASS(1),
    //时间段通过
    TIME_PASS(2);
    @Getter
    private Integer skipVerify;

    WeEmployCodeSkipVerifyEnum(Integer skipVerify) {
        this.skipVerify = skipVerify;
    }

    /**
     * 校验是否可设置为自动通过好友
     *
     * @param skipVerify      通过好友类型
     * @param effectTimeOpen  开启时间
     * @param effectTimeClose 结束时间
     * @return Boolean
     */
    public static Boolean isPassByNow(Integer skipVerify, String effectTimeOpen, String effectTimeClose) {
        if (skipVerify == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (NO_PASS.getSkipVerify().equals(skipVerify)) {
            return false;
        }
        if (ALL_PASS.getSkipVerify().equals(skipVerify)) {
            return true;
        }
        //当skipVerify=2时，需要校验当前时间是否在指定时间范围内
        if (StringUtils.isBlank(effectTimeOpen) || StringUtils.isBlank(effectTimeClose)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return DateUtils.verifyCurrTimeEithinTimeRange(effectTimeOpen, effectTimeClose);
    }
}
