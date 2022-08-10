package com.easywecom.common.enums.code;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

/**
 * 群活码类型枚举
 *
 * @author tigger
 * 2022/2/9 19:23
 **/
@Slf4j
public enum GroupCodeTypeEnum {
    /**
     * 群二维码活码
     */
    GROUP_QR(1),
    /**
     * 企业微信活码
     */
    CORP_QR(2),
    ;


    private Integer type;

    GroupCodeTypeEnum(Integer type) {
        this.type = type;
    }


    public Integer getType() {
        return type;
    }

    public static GroupCodeTypeEnum getByType(Integer type) {
        if (type == null) {
            return null;
        }
        for (GroupCodeTypeEnum value : GroupCodeTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static void assertNotNull(Integer createType) {
        if (createType == null) {
            log.error("com.easywecom.common.enums.code.GroupCodeTypeEnum.assertNotNull: createType is null");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        for (GroupCodeTypeEnum value : GroupCodeTypeEnum.values()) {
            if (value.getType().equals(createType)) {
                return;
            }
        }
        log.error("com.easywecom.common.enums.code.GroupCodeTypeEnum.assertNotNull: createType is not exist");
        throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
    }
}
