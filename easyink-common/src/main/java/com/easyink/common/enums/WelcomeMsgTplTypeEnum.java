package com.easyink.common.enums;

import com.easyink.common.exception.CustomException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 运营中心-欢迎语模板类型和长度限制枚举值
 *
 * @author Society my sister Li
 * @date 2021/9/13
 */
public enum WelcomeMsgTplTypeEnum {

    //员工欢迎语
    EMP_WELCOME(1, 1000),

    //入群欢迎语
    GROUP_WELCOME(2, 1000),

    //活码欢迎语
    LIVE_CODE(3, 1500),
    ;
    @Getter
    private final Integer type;
    @Getter
    private final Integer length;

    WelcomeMsgTplTypeEnum(Integer type, Integer length) {
        this.type = type;
        this.length = length;
    }


    /**
     * 验证欢迎语长度
     *
     * @param type 请求类型
     * @param msg  内容
     */
    public static void validLength(Integer type, String msg) {
        if (type == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        int length = StringUtils.isBlank(msg) ? 0 : msg.length();
        for (WelcomeMsgTplTypeEnum typeEnum : values()) {
            if (typeEnum.type.equals(type)) {
                if (length > typeEnum.length) {
                    throw new CustomException(ResultTip.TIP_DEFAULT_MSG_TOO_LONG);
                }
                return;
            }
        }
        throw new CustomException(ResultTip.TIP_MSG_TYPE_NOT_FUND);
    }

}
